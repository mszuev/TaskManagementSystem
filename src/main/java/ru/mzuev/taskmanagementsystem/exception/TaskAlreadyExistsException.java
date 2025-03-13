package ru.mzuev.taskmanagementsystem.exception;

public class TaskAlreadyExistsException extends RuntimeException {
    public TaskAlreadyExistsException(String title) {
        super("Задача с названием " + title + " уже существует");
    }
}