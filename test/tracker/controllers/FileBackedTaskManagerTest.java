package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File file;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("tasksTest", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void tearDown() {
        file.delete();
    }

    @Test
    void addTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Epic 1", "Description Epic 1");
        manager.addEpic(epic);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertTrue(epics.contains(epic));
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Epic 1", "Description Epic 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertTrue(subtasks.contains(subtask));
    }

    @Test
    void updateTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);

        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);
        manager.deleteTaskById(task.getId());

        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.DONE);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.deleteTasks();

        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Epic 1", "Description Epic 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.DONE, epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.deleteSubtasks();

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void deleteEpics() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.deleteEpics();

        List<Epic> epics = manager.getAllEpics();
        assertTrue(epics.isEmpty());
    }

    @Test
    void save() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
    }

    @Test
    void loadFromFile() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
    }
}
