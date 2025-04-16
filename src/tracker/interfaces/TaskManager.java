package tracker.interfaces;

import tracker.exceptions.TaskOverlapException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTaskById(int id);

    void addTask(Task task) throws TaskOverlapException;

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask) throws TaskOverlapException;

    void updateTask(Task task);

    void deleteTaskById(int id);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();

    void updateTimeEpic(Epic epic);

    List<Task> getPrioritizedTasks();
}