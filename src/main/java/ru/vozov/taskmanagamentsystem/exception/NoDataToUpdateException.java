package ru.vozov.taskmanagamentsystem.exception;

public class NoDataToUpdateException extends RuntimeException {
    public NoDataToUpdateException(String message) {
        super(message);
    }
}
