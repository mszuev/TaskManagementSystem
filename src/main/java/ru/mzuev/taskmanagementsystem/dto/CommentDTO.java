package ru.mzuev.taskmanagementsystem.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long taskId;
    private Long userId;
}