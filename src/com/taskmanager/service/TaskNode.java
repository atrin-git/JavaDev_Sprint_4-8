package com.taskmanager.service;

import com.taskmanager.model.AbstractTask;

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
