package ru.mzuev.taskmanagementsystem.dto;

public class CommentRequest {
    private String content;
    private Long taskId;

    public CommentRequest() {}

    public CommentRequest(String content, Long taskId) {
        this.content = content;
        this.taskId = taskId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
