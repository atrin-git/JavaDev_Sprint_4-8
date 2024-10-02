package com.taskmanager;

import com.taskmanager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Тестирование функционала
        Tests tests = new Tests();

        // Проверка добавления задач
        tests.testAdd(taskManager);
        tests.testGetTasks(taskManager);

        // Проверка изменения задач
        tests.testEdit(taskManager);
        tests.testGetTasks(taskManager);

        // Проверка обвновления статусов
        tests.updateStatuses(taskManager);

        // Проверка удаления
        tests.testDelete(taskManager);
        tests.testGetTasks(taskManager);
    }

}
