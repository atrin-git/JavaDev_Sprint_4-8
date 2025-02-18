package com.taskmanager;

import com.taskmanager.service.HttpTaskServer;
import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = Files.createTempFile("task", ".csv").toFile();
        TaskManager taskManager = Managers.loadFromFile(file);

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

        new Scenarios().testAdd(taskManager);

        server.stop();

        file.deleteOnExit();
    }

}
