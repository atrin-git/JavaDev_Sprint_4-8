package com.taskmanager.service;

/**
 * Исключение в случае проблем с сохранением данных из таск-менеджера в файл
 */
public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(String message) {
        super(message);
    }

}
