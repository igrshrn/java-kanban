package tracker.controllers;

import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;
import tracker.util.TaskType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    public void save() {
        List<String> lines = Stream.concat(
                        Stream.concat(
                                getAllTasks().stream(),
                                getAllEpics().stream()
                        ),
                        getAllSubtasks().stream()
                )
                .map(this::taskToString)
                .collect(Collectors.toList());

        lines.addFirst("id,type,name,status,duration,startTime,description,epic");
        if (!file.exists()) {
            throw new ManagerSaveException("Файл не существует", new FileNotFoundException());
        }
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.stream()
                    .skip(1) // Пропускаем заголовок
                    .map(FileBackedTaskManager::taskFromString)
                    .forEach(task -> {
                        manager.setIdCounter(task.getId() - 1);

                        if (task.getType() == TaskType.TASK) {
                            manager.addTask(task);
                        } else if (task.getType() == TaskType.EPIC) {
                            manager.addEpic((Epic) task);
                        } else if (task.getType() == TaskType.SUBTASK) {
                            manager.addSubtask((Subtask) task);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла", e);
        }

        return manager;
    }


    private static Task taskFromString(String value) {
        String[] parts = value.split(",");
        /*int count = 0;
        for (String part : parts) {
            System.out.println("key: " + count + "-part: " + part);
            count++;
        }*/
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        Duration duration = null;
        if (!parts[4].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(parts[4]));
        }

        LocalDateTime startTime = null;
        if (!parts[5].equals("null")) {
            startTime = LocalDateTime.parse(parts[5]);
        }
        String description = parts[6];

        Task task;
        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[7]);
            task = new Subtask(title, description, status, epicId, duration, startTime);

        } else if (type == TaskType.EPIC) {
            task = new Epic(title, description);
        } else {
            task = new Task(title, description, status, duration, startTime);
        }

        task.setId(id);

        return task;
    }

    private String taskToString(Task task) {
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = "," + ((Subtask) task).getEpicId();
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDuration().toMinutes(),
                task.getStartTime(),
                task.getDescription(),
                epicId);
    }

    public static void main(String[] args) {
        try {
            File file = File.createTempFile("ytasks", ".csv");
            System.out.println(file.toPath());
            // Создаем первый менеджер и сохраняем через него задачи в файл
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
            Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
            Task task3 = new Task("Task 3", "Description 3", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));
            Task task4 = new Task("Task 4", "Description 4", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(3));
            Task task5 = new Task("Task 5", "Description 5", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(4));

            manager.addTask(task1);
            manager.addTask(task2);
            manager.addTask(task3);
            //manager.deleteTaskById(2);
            manager.addTask(task4);
            //manager.deleteTaskById(3);
            manager.addTask(task5);

            Epic epic1 = new Epic("Epic 1", "Description Epic 1");
            Epic epic2 = new Epic("Epic 2", "Description Epic 2");

            manager.addEpic(epic1);
            manager.addEpic(epic2);

            Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now().plusHours(9));
            Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(10));

            manager.addSubtask(subtask1);
            manager.addSubtask(subtask2);
            FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
            //manager.deleteTasks();


            // Создаем новый менеджер из файла


            System.out.println("\nЗадачи в старом менеджере:");
            for (Task task : manager.getAllTasks()) {
                System.out.println(task);
            }

            System.out.println("\nЗадачи в новом менеджере:");
            for (Task task : newManager.getAllTasks()) {
                System.out.println(task);
            }

            System.out.println("\nЭпики и их подзадачи в старом менеджере:");
            for (Epic epic : manager.getAllEpics()) {
                System.out.println(epic);
                System.out.println(epic.getSubtaskIds());
                System.out.println("===");
            }

            System.out.println("\nЭпики и их подзадачи в новом менеджере:");
            for (Epic epic : newManager.getAllEpics()) {
                System.out.println(epic);
                System.out.println(epic.getSubtaskIds());
                System.out.println("===");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
}