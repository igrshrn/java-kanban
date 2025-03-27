package tracker.controllers;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testEpicStatusNew() {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void testEpicStatusDone() {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testEpicStatusWithInProgressSubtasks() {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testTaskOverlap() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));

        taskManager.addTask(task1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task2);
        }, "Задачи пересекаются");
    }
}