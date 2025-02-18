package com.taskmanager.service.exceptions;

public class WithouIdException extends RuntimeException {
    public WithouIdException(String message) {
        super(message);
    }
}
