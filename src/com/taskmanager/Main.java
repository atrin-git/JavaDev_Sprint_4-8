package com.taskmanager;

import com.taskmanager.service.Managers;
import com.taskmanager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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
    }

}
