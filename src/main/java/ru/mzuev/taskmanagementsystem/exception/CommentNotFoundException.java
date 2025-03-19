package ru.mzuev.taskmanagementsystem.exception;

public class CommentNotFoundException extends RuntimeException {
    private final Long commentId;

    public CommentNotFoundException(Long commentId) {
        super("Комментарий не найден с id " + commentId);
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
    }
}