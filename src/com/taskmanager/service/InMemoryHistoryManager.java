package com.taskmanager.service;

import com.taskmanager.model.AbstractTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_CAPACITY = 10;
    private final List<AbstractTask> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    public static int getHistoryCapacity() {
        return HISTORY_CAPACITY;
    }

    @Override
    public void add(AbstractTask abstractTask) {
        if (history.size() == HISTORY_CAPACITY) {
            history.removeFirst();
        }
        history.addLast(abstractTask);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return this.history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(history);
    }

    @Override
    public String toString() {
        return InMemoryHistoryManager.class.getName() + " {" +
                "history = " + history +
                '}';
    }
}
