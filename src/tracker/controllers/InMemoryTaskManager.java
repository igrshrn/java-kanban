package tracker.controllers;

import tracker.exceptions.TaskOverlapException;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.IntervalManager;
import tracker.util.Managers;
import tracker.util.TaskStatus;
import tracker.util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private IntervalManager intervalManager = new IntervalManager();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        try {
            doesTaskOverlapWithExisting(task);
            final int id = ++idCounter;
            task.setId(id);
            tasks.put(id, task);
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        try {
            doesTaskOverlapWithExisting(epic);
            final int id = ++idCounter;
            epic.setId(id);
            epics.put(id, epic);
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        try {
            doesTaskOverlapWithExisting(subtask);
            final int id = ++idCounter;
            subtask.setId(id);
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask.getId());
                updateEpicStatus(epic);
                updateTimeEpic(epic);
            }
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateTimeEpic(Epic epic) {
        ArrayList<Subtask> subtasksList = getSubtasksOfEpic(epic.getId());

        Optional<LocalDateTime> optionalStartTime = subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());

        Optional<LocalDateTime> optionalEndTime = subtasksList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());

        epic.setStartTime(optionalStartTime.orElse(null));
        epic.setEndTime(optionalEndTime.orElse(null));

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            Duration totalDuration = subtasksList.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setDuration(totalDuration);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    protected void doesTaskOverlapWithExisting(Task task) throws TaskOverlapException {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> doTasksOverlap(task, existingTask));

        if (isOverlapping) {
            throw new TaskOverlapException("Задача: " + task.getTitle() + " пересекается с одной из задач.");
        } else {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    // TODO подумать над интервалами
    private void isIntervalFree(Task task) throws TaskOverlapException {
        boolean isOverlapping = intervalManager.isIntervalFree(task.getStartTime(), task.getDuration());
        if (isOverlapping) {
            throw new TaskOverlapException("Задача: " + task.getTitle() + " пересекается с одной из задач.");
        }
    }

    protected boolean doTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime task1End = task1.getEndTime();
        LocalDateTime task2End = task2.getEndTime();

        return task1End != null && task2End != null &&
                task1.getStartTime().isBefore(task2End) &&
                task2.getStartTime().isBefore(task1End);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateTask(Task task) {
        try {
            prioritizedTasks.removeIf(existingTask -> existingTask.getId() == task.getId());
            doesTaskOverlapWithExisting(task);
            if (task.getType().equals(TaskType.SUBTASK)) {
                Subtask subtask = (Subtask) task;
                subtasks.put(task.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    updateEpicStatus(epic);
                    updateTimeEpic(epic);
                }
            } else if (task.getType().equals(TaskType.EPIC)) {
                epics.put(task.getId(), (Epic) task);
            } else {
                tasks.put(task.getId(), task);
            }
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            epic.getSubtaskIds().forEach(subtaskId -> {
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
                updateTimeEpic(epic);
            }
            prioritizedTasks.removeIf(task -> task.getId() == id);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.forEach((key, task) -> {
            historyManager.remove(key);
            prioritizedTasks.removeIf(task::equals);
        });
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        epics.values().forEach(epic -> {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic);
            updateTimeEpic(epic);
        });
        subtasks.forEach((key, task) -> {
            historyManager.remove(key);
            prioritizedTasks.removeIf(task::equals);
        });
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        deleteSubtasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subtasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasksList.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

        boolean allDone = subtasksList.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }
}
