package ru.mzuev.taskmanagementsystem.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {
    private final Long commentId;

    public CommentNotFoundException(Long commentId) {
        super("Комментарий не найден с id " + commentId);
        this.commentId = commentId;
    }
}