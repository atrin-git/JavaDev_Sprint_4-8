package com.taskmanager.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void checkCreateInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Таск-менеджер должен был быть проинициализированным");
        assertTrue(taskManager.toString().contains(taskManager.getClass().getName()));
        assertEquals(taskManager.getClass().getSimpleName(), InMemoryTaskManager.class.getSimpleName(),
                "Наименования классов не совпадают");
    }

    @Test
    public void checkCreateInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории должен был быть проинициализированным");
        assertEquals(historyManager.getClass().getSimpleName(), InMemoryHistoryManager.class.getSimpleName(),
                "Наименования классов не совпадают");
    }

    @Test
    public void checkCreateFileBackedTaskManager() {
        File file = null;
        try {
            file = Files.createTempFile("task", ".csv").toFile();
            TaskManager taskManager = Managers.loadFromFile(file);

            assertNotNull(taskManager, "Таск-менеджер с сохранением в файл должен был быть проинициализированным");
            assertEquals(taskManager.getClass().getSimpleName(), FileBackedTaskManager.class.getSimpleName(),
                    "Наименования классов не совпадают");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (file != null) {
                file.deleteOnExit();
            }
        }
    }

}