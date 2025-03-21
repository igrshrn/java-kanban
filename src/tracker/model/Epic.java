package tracker.model;

import java.util.ArrayList;

import tracker.util.TaskStatus;
import tracker.util.TaskType;

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
        if (subtaskId == this.getId()) {
            throw new IllegalArgumentException("Эпик не может быть добавлен в самого себя в виде подзадачи.");
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
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
