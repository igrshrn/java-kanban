public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(taskManager.generateId(), "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(taskManager.generateId(), "Task 2", "Description of Task 2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic(taskManager.generateId(), "Epic 1", "Description of Epic 1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask(taskManager.generateId(), "Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(taskManager.generateId(), "Subtask 2", "Description of Subtask 2", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic(taskManager.generateId(), "Epic 2", "Description of Epic 2");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask(taskManager.generateId(), "Subtask 3", "Description of Subtask 3", TaskStatus.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println("All tasks: " + taskManager.getAllTasks());
        System.out.println("All epics: " + taskManager.getAllEpics());
        System.out.println("All subtasks: " + taskManager.getAllSubtasks() + "\n");

        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(subtask1);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask3);

        System.out.println("Updated tasks: " + taskManager.getAllTasks());
        System.out.println("Updated epics: " + taskManager.getAllEpics()  + "\n");

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteTaskById(epic1.getId());

        System.out.println("Remaining tasks: " + taskManager.getAllTasks());
        System.out.println("Remaining epics: " + taskManager.getAllEpics());
        System.out.println("Remaining subtasks: " + taskManager.getAllSubtasks());

        taskManager.clearAllTasks();
        System.out.println("All tasks: " + taskManager.getAllTasks());
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());
        System.out.println("All epics: " + taskManager.getAllEpics());
    }

}
