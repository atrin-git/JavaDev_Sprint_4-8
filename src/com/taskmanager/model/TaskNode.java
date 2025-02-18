package com.taskmanager.model;

public class TaskNode {
    private AbstractTask task;
    private TaskNode prev;
    private TaskNode next;

    public TaskNode(AbstractTask task) {
        this(task, null);
    }

    public TaskNode(AbstractTask task, TaskNode prev) {
        this.task = task;
        this.prev = prev;
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
