package ru.mzuev.taskmanagementsystem.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему комментарию.
 */
@Getter
public class CommentNotFoundException extends RuntimeException {

    /**
     * Идентификатор комментария, который не был найден.
     */
    private final Long commentId;

    /**
     * Создает исключение для указанного комментария.
     *
     * @param commentId Идентификатор несуществующего комментария.
     */
    public CommentNotFoundException(Long commentId) {
        super("Комментарий не найден с id " + commentId);
        this.commentId = commentId;
    }
}