package com.taskmanager.service;

import com.taskmanager.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, TaskNode> idTaskMap;
    private TaskNode head;
    private TaskNode tail;

    public InMemoryHistoryManager() {
        this.idTaskMap = new HashMap<>();
    }

    @Override
    public void add(AbstractTask abstractTask) {
        int id = abstractTask.getId();
        remove(id);

        linkLast(abstractTask);
        idTaskMap.put(id, tail);
    }

    @Override
    public void remove(int id) {
        final TaskNode nodeToRemove = idTaskMap.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return getTasks();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(getTasks(), that.getTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTasks());
    }

    @Override
    public String toString() {
        return InMemoryHistoryManager.class.getName() + " {" +
                "history = " + getTasks() +
                '}';
    }

    private void linkLast(AbstractTask task) {
        if (head == null) {
            head = new TaskNode(task);
            tail = head;
        } else {
            tail.setNext(new TaskNode(task, tail));
            tail = tail.getNext();
        }
    }

    private List<AbstractTask> getTasks() {
        List<AbstractTask> tasks = new LinkedList<>();
        TaskNode current = head;

        while (current != null) {
            tasks.addLast(current.getTask());
            current = current.getNext();
        }

        return tasks;
    }

    private void removeNode(TaskNode taskNode) {
        TaskNode next = taskNode.getNext();
        TaskNode prev = taskNode.getPrev();

        if (next == null && prev == null) {
            head = null;
            tail = null;
        } else if (prev == null) {
            head = next;
            head.setPrev(null);
        } else if (next == null) {
            tail = prev;
            tail.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }
}
