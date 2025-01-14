package tracker.model;

import java.util.ArrayList;

import tracker.util.TaskStatus;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "tracker.model.Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                "} ";
    }
}
