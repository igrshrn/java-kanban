package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.interfaces.HistoryManager;
import tracker.model.Task;
import tracker.util.Managers;
import tracker.util.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Test add", "Test add description", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task, history.get(0), "Задача должна быть добавлена в историю.");

    }

    @Test
    void removeOldestTask() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Test removeOldestTask " + i, "Test removeOldestTask " + i + " description", TaskStatus.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(10, history.size(), "История должна содержать 10 задач.");
        assertEquals(2, history.get(0).getId(), "Первая задача должна быть второй добавленной задачей.");
        assertEquals(11, history.get(9).getId(), "Последняя задача должна быть одиннадцатой добавленной задачей.");
    }

    @Test
    void getHistory() {
        Task task1 = new Task("Test getHistory 1", "Test getHistory 1 description", TaskStatus.NEW);
        Task task2 = new Task("Test getHistory 2", "Test getHistory 2 description", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertEquals(task1, history.get(0), "Первая задача должна быть добавлена в историю.");
        assertEquals(task2, history.get(1), "Вторая задача должна быть добавлена в историю.");
    }
}