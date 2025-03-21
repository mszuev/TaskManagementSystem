package ru.mzuev.taskmanagementsystem.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String email;

    public UserAlreadyExistsException(String email) {
        super("Пользователь с email " + email + " уже существует");
        this.email = email;
    }
}