import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.util.Managers;
import tracker.util.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Создание подзадач для эпика 1
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", TaskStatus.NEW, epic1.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Запрос задач в разном порядке
        manager.getTaskById(subtask1.getId());
        manager.getTaskById(epic2.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(epic1.getId());
        manager.getTaskById(subtask3.getId());
        manager.getTaskById(subtask2.getId());
        manager.getTaskById(subtask1.getId());

        // Вывод истории
        System.out.println("История после рандомных вызовов тасок:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        // Удаление задачи
        manager.deleteTaskById(task1.getId());

        // Вывод истории после удаления задачи
        System.out.println("\nИстория после удаления Task 1:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        // Удаление эпика
        manager.deleteTaskById(epic1.getId());

        // Вывод истории после удаления эпика
        System.out.println("\nИстория после удаления Epic 1:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }

}
