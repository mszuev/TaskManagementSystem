package ru.mzuev.taskmanagementsystem.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super("Комментарий не найден с id " + commentId);
    }
}