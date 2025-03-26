package ru.mzuev.taskmanagementsystem.exception;

/**
 * Исключение, выбрасываемое при попытке создать задачу с уже существующим названием.
 */
public class TaskAlreadyExistsException extends RuntimeException {

    /**
     * Создает исключение с сообщением о конфликте названия задачи.
     *
     * @param title Название задачи, которое уже существует.
     */
    public TaskAlreadyExistsException(String title) {
        super("Задача с названием " + title + " уже существует");
    }
}