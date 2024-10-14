package com.taskmanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void checkCreateInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Таск-менеджер должен был быть проинициализированным");
        assertTrue(taskManager.toString().contains(taskManager.getClass().getName()));
    }

    @Test
    public void checkCreateInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории должен был быть проинициализированным");
        assertTrue(historyManager.toString().contains(historyManager.getClass().getName()));
    }

}