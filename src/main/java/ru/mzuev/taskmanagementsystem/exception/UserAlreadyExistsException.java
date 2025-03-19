package ru.mzuev.taskmanagementsystem.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private final String email;

    public UserAlreadyExistsException(String email) {
        super("Пользователь с email " + email + " уже существует");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}