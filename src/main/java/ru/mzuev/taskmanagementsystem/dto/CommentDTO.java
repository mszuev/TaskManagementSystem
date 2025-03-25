package ru.mzuev.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;

    @NotBlank(message = "{validation.comment.content.required}")
    @Size(max = 1000, message = "{validation.comment.content.size}")
    private String content;

    @NotNull(message = "{validation.comment.taskId.required}")
    @Positive(message = "{validation.comment.taskId.positive}")
    private Long taskId;

    private Long userId;
}