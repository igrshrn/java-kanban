package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Task;
import tracker.util.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() {
        try {
            file = File.createTempFile("tasksTest", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void tearDown() {
        file.delete();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void saveAndLoad() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
    }

    @Test
    void testExceptionOnSave() {
        assertTrue(file.exists(), "Файл должен существовать перед удалением");
        assertThrows(ManagerSaveException.class, () -> {
            file.delete();
            taskManager.save();
        }, "Ошибка сохранения файла должна приводить к исключению");
    }

    @Test
    void testExceptionOnLoad() {
        assertThrows(ManagerSaveException.class, () -> {
            file.delete();
            FileBackedTaskManager.loadFromFile(file);
        }, "Ошибка чтения файла должна приводить к исключению");
    }
}
