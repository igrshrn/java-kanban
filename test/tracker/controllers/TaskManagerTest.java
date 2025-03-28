package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void getAllTasks() {
        Task task = new Task("Test getAllTasks", "Test getAllTasks description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getAllEpics() {
        Epic epic = new Epic("Test getAllEpics", "Test getAllEpics description");
        taskManager.addEpic(epic);

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void getAllSubtasks() {
        Epic epic = new Epic("Test getAllSubtasks", "Test getAllSubtasks description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test getAllSubtasks", "Test getAllSubtasks description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Test getTaskById", "Test getTaskById description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Test getEpicById", "Test getEpicById description");
        taskManager.addEpic(epic);

        final Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Test getSubtaskById", "Test getSubtaskById description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test getSubtaskById", "Test getSubtaskById description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void addTask() {
        Task task = new Task("Test addTask", "Test addTask description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Test addSubtask", "Test addSubtask description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addSubtask", "Test addSubtask description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test updateTask", "Test updateTask description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        Task updatedTask = new Task("Test updateTask updated", "Test updateTask updated description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.now());
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test updateSubtask", "Test updateSubtask description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test updateSubtask", "Test updateSubtask description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Test updateSubtask updated", "Test updateSubtask updated description", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        updatedSubtask.setId(subtask.getId());
        taskManager.updateTask(updatedSubtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test updateEpic", "Test updateEpic description");
        taskManager.addEpic(epic);

        Epic updatedEpic = new Epic("Test updateEpic updated", "Test updateEpic updated description");
        updatedEpic.setId(epic.getId());
        taskManager.updateTask(updatedEpic);

        final Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(updatedEpic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Test deleteTaskById", "Test deleteTaskById description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        taskManager.deleteTaskById(task.getId());

        final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Задача не удалена.");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Test deleteEpicById", "Test deleteEpicById description");
        taskManager.addEpic(epic);

        taskManager.deleteTaskById(epic.getId());

        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Эпик не удален.");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Test deleteSubtaskById", "Test deleteSubtaskById description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test deleteSubtaskById", "Test deleteSubtaskById description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        taskManager.deleteTaskById(subtask.getId());

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадача не удалена.");
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task("Test deleteTasks 1", "Test deleteTasks 1 description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Test deleteTasks 2", "Test deleteTasks 2 description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteTasks();

        final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Задачи не удалены.");
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Test deleteSubtasks", "Test deleteSubtasks description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test deleteSubtasks 1", "Test deleteSubtasks 1 description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test deleteSubtasks 2", "Test deleteSubtasks 2 description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtasks();

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадачи не удалены.");
    }

    @Test
    void deleteEpics() {
        Epic epic1 = new Epic("Test deleteEpics 1", "Test deleteEpics 1 description");
        Epic epic2 = new Epic("Test deleteEpics 2", "Test deleteEpics 2 description");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.deleteEpics();

        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Эпики не удалены.");
    }

    @Test
    void getSubtasksOfEpic() {
        Epic epic = new Epic("Test getSubtasksOfEpic", "Test getSubtasksOfEpic description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test getSubtasksOfEpic 1", "Test getSubtasksOfEpic 1 description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test getSubtasksOfEpic 2", "Test getSubtasksOfEpic 2 description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        final List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
        assertEquals(subtask2, subtasks.get(1), "Подзадачи не совпадают.");
    }

    @Test
    void getHistory() {
        Task task = new Task("Test getHistory", "Test getHistory description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        final List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task, history.get(0), "Задача должна быть добавлена в историю.");
    }

    @Test
    void getHistoryAfterRemove() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(3));
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(4));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(subtask1.getId());
        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(subtask3.getId());

        taskManager.deleteTaskById(task1.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(6, history.size(), "После удаления Task 1 в истории должно остаться 6 записей");

        taskManager.deleteTaskById(subtask3.getId());
        history = taskManager.getHistory();
        assertEquals(5, history.size(), "После удаления Subtask 3 в истории должно остаться 5 записей");

        taskManager.deleteTaskById(epic1.getId());
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "После удаления Epic 1 в истории должно остаться 2 записи");
    }

    @Test
    void getHistoryAfterRemoveAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        taskManager.deleteTasks();
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "После удаления всех тасок, история должна оказаться пустой");
    }

    @Test
    void getHistoryAfterRemoveAllSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(subtask1.getId());
        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(subtask3.getId());

        taskManager.deleteSubtasks();
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "После удаления всех сабтасок в истории должны оказаться 2 эпика");
    }

    @Test
    void getHistoryAfterRemoveAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(epic2.getId());

        taskManager.deleteEpics();
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "После удаления всех сабтасок в истории должны оказаться 2 эпика");
    }
}

