package ru.mzuev.taskmanagementsystem.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к ресурсу без необходимых прав.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением.
     *
     * @param message Описание причины ошибки.
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}