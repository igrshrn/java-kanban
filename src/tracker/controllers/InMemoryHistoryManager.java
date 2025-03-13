package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Task;
import tracker.util.CustomLinkedList.CustomLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList customLinkedList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        customLinkedList.add(task.getId(), task);
    }

    @Override
    public void remove(int id) {
        customLinkedList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getDataList();
    }

}