package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return subtasks.get(id);
    }

    public void addTask(Task task) {
        final int id = ++idCounter;
        task.setId(id);
        tasks.put(id, task);
    }

    public void addEpic(Epic epic) {
        final int id = ++idCounter;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addSubtask(Subtask subtask) {
        final int id = ++idCounter;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    public void updateTask(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(task.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        } else if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        Epic epic = epics.get(epicId);
        if (epic == null) {
            return result;
        }

        for (int subtaskId : epic.getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}

