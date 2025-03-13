package ru.mzuev.taskmanagementsystem.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long taskId) {
        super("Задача не найдена с id " + taskId);
    }
}