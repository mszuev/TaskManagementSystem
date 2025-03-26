package ru.mzuev.taskmanagementsystem.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему пользователю.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Создает исключение с указанием email.
     *
     * @param email Email, по которому пользователь не найден.
     */
    public UserNotFoundException(String email) {
        super("Пользователь не найден с email " + email);
    }

    /**
     * Создает исключение с указанием идентификатора.
     *
     * @param userId Идентификатор, по которому пользователь не найден.
     */
    public UserNotFoundException(Long userId) {
        super("Пользователь не найден с id " + userId);
    }
}