package com.taskmanager.service;

import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    @Test
    public void checkCreateHttpTaskServer() throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);

        assertNotNull(server, "Сервер должен был быть проинициализированным");
        assertEquals(server.getClass().getSimpleName(), HttpTaskServer.class.getSimpleName(),
                "Наименования классов не совпадают");
    }
}
