package com.taskmanager.service;

import com.taskmanager.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, TaskNode> idTaskMap;
    private TaskNode head;
    private TaskNode tail;

    public InMemoryHistoryManager() {
        this.idTaskMap = new HashMap<>();
        this.head = null;
        this.tail = null;
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
        List<AbstractTask> tasks = new ArrayList<>();
        TaskNode current = head;

        while(current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }

        return tasks;
    }

    private void removeNode(TaskNode taskNode) {
        if (taskNode.getNext() == null && taskNode.getPrev() == null) {
            head = null;
            tail = null;
        } else if (taskNode.getPrev() == null) {
            head = taskNode.getNext();
            head.setPrev(null);
        } else if (taskNode.getNext() == null) {
            tail = taskNode.getPrev();
            tail.setNext(null);
        } else {
            taskNode.getPrev().setNext(taskNode.getNext());
            taskNode.getNext().setPrev(taskNode.getPrev());
        }
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
        if (idTaskMap.containsKey(id)) {
            removeNode(idTaskMap.get(id));
            idTaskMap.remove(id);
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
}

class TaskNode {
    private AbstractTask task;
    private TaskNode prev;
    private TaskNode next;

    public TaskNode(AbstractTask task) {
        this(task, null);
    }

    public TaskNode(AbstractTask task, TaskNode prev) {
        this.task = task;
        this.prev = prev;
        this.next = null;
    }

    public AbstractTask getTask() {
        return task;
    }

    public TaskNode getPrev() {
        return prev;
    }

    public TaskNode getNext() {
        return next;
    }

    public void setPrev(TaskNode prev) {
        this.prev = prev;
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }
}
