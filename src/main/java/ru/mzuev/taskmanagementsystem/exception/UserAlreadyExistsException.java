package ru.mzuev.taskmanagementsystem.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке регистрации пользователя с уже существующим email.
 */
@Getter
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Конфликтующий email пользователя.
     */
    private final String email;

    /**
     * Создает исключение с указанием email.
     *
     * @param email Email, который уже зарегистрирован.
     */
    public UserAlreadyExistsException(String email) {
        super("Пользователь с email " + email + " уже существует");
        this.email = email;
    }
}