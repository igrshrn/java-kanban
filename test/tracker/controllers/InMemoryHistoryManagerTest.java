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

        System.out.println(history.size());
        System.out.println(history.get(0).getId());
        assertNotNull(history, "История не пустая.");
        assertEquals(11, history.size(), "История должна содержать 11 задач.");
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

    @Test
    void getHistoryAfterAdd() {
        Task task1 = new Task("Test getHistory 1", "Test getHistory 1 description", TaskStatus.NEW);
        Task task2 = new Task("Test getHistory 2", "Test getHistory 2 description", TaskStatus.NEW);
        Task task3 = new Task("Test getHistory 3", "Test getHistory 3 description", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Первая задача должна быть добавлена в историю.");
        assertEquals(task2, history.get(1), "Вторая задача должна быть добавлена в историю.");

        historyManager.add(task3);
        history = historyManager.getHistory();
        assertEquals(task3, history.get(2), "Третья задача должна быть добавлена в историю.");

        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(task2, history.get(0), "После перестановки первой в истории должна быть вторая.");
        assertEquals(task3, history.get(1), "После перестановки второй в истории должна быть третья.");
        assertEquals(task1, history.get(2), "После перестановки третьей в истории должна быть первая.");
    }

}