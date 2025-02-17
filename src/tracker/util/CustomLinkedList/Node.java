package tracker.util.CustomLinkedList;

import tracker.model.Task;

public class Node {
    public Task task;
    public Node prev;
    public Node next;

    public Node() {
        this(null);
    }

    public Node(Task task) {
        this.task = task;
    }
}
