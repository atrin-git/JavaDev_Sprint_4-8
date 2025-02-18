package com.taskmanager.service.exceptions;

/**
 * Исключение в случае проблем с чтением таск-менеджером из файла
 */
public class ManagerReadException extends RuntimeException {

    public ManagerReadException() {
        super();
    }

    public ManagerReadException(String message) {
        super(message);
    }

}
