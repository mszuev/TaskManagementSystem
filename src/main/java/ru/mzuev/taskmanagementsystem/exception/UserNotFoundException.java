package ru.mzuev.taskmanagementsystem.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Пользователь не найден с email " + email);
    }
}