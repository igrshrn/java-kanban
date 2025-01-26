package tracker.interfaces;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Subtask> getAllSubtasks();
    Task getTaskById(int id);
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);
    void updateTask(Task task);
    void deleteTaskById(int id);
    void deleteTasks();
    void deleteSubtasks();
    void deleteEpics();
    ArrayList<Subtask> getSubtasksOfEpic(int epicId);
    List<Task> getHistory();
}