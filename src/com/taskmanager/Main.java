package com.taskmanager;

import com.taskmanager.service.Managers;
import com.taskmanager.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = Files.createTempFile("task", ".csv").toFile();
        TaskManager taskManager = Managers.loadFromFile(file);

        // Тестирование функционала
        Scenarios scenarios = new Scenarios();

        // Проверка добавления задач
        scenarios.testAdd(taskManager);
        scenarios.testGetTasks(taskManager);

        // Проверка изменения задач
        scenarios.testEdit(taskManager);
        scenarios.testGetTasks(taskManager);

        // Проверка обновления статусов
        scenarios.updateStatuses(taskManager);

        // Проверка просмотра истории
        Scenarios.testHistory(taskManager);

        // Проверка удаления
        scenarios.testDelete(taskManager);
        scenarios.testGetTasks(taskManager);

        file.deleteOnExit();
    }

}
