package com.taskmanager.service.managers;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
