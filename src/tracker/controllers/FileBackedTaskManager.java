package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;
import tracker.util.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
        List<String> lines = new ArrayList<>();
        lines.add("id,type,name,status,description,epic");

        for (Task task : getAllTasks()) {
            lines.add(taskToString(task));
        }

        for (Epic epic : getAllEpics()) {
            lines.add(taskToString(epic));
        }

        for (Subtask subtask : getAllSubtasks()) {
            lines.add(taskToString(subtask));
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
            lines.remove(0); // Удаляем шапку

            for (String line : lines) {
                Task task = taskFromString(line);
                if (task.getType() == TaskType.TASK) {
                    manager.addTask(task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.addEpic((Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.addSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла", e);
        }

        return manager;
    }

    private String taskToString(Task task) {
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = "," + ((Subtask) task).getEpicId();
        }
        return String.format("%d,%s,%s,%s,%s%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }

    private static Task taskFromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[5]);
            task = new Subtask(title, description, status, epicId);
        } else if (type == TaskType.EPIC) {
            task = new Epic(title, description);
        } else {
            task = new Task(title, description, status);
        }

        task.setId(id);
        return task;
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void main(String[] args) {
        try {
            File file = File.createTempFile("tasks", ".csv");

            // Создаем первый менеджер и сохраняем через него задачи в файл
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
            Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

            manager.addTask(task1);
            manager.addTask(task2);

            Epic epic1 = new Epic("Epic 1", "Description Epic 1");
            Epic epic2 = new Epic("Epic 2", "Description Epic 2");

            manager.addEpic(epic1);
            manager.addEpic(epic2);

            Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic1.getId());
            Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.NEW, epic1.getId());

            manager.addSubtask(subtask1);
            manager.addSubtask(subtask2);

            // Сохранение данных
            manager.save();

            // Создаем новый менеджер из файла
            FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);

            // Проверяем, что все задачи сохранены
            System.out.println("Задачи в новом менеджере:");
            for (Task task : newManager.getAllTasks()) {
                System.out.println(task);
            }

            System.out.println("\nЭпики в новом менеджере:");
            for (Epic epic : newManager.getAllEpics()) {
                System.out.println(epic);
            }

            System.out.println("\nПодзадачи в новом менеджере:");
            for (Subtask subtask : newManager.getAllSubtasks()) {
                System.out.println(subtask);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
}