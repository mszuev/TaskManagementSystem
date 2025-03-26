package ru.mzuev.taskmanagementsystem.exception;

/**
 * Исключение, выбрасываемое при неверных учетных данных аутентификации.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Создает исключение с сообщением по умолчанию.
     */
    public InvalidCredentialsException() {
        super("Неверные email или пароль");
    }
}