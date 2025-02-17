package tracker.util.CustomLinkedList;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private final Node head;
    private final Node tail;

    public CustomLinkedList() {
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    public void add(int id, Task task) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
        }
        linkLast(id, task);
    }

    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    public List<Task> getDataList() {
        List<Task> dataList = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            dataList.add(current.task);
            current = current.next;
        }
        return dataList;
    }

    private void linkLast(int id, Task task) {
        Node newNode = new Node(task);
        Node last = tail.prev;
        last.next = newNode;
        newNode.prev = last;
        newNode.next = tail;
        tail.prev = newNode;
        nodeMap.put(id, newNode);
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
        nodeMap.remove(node.task.getId());
    }
}
