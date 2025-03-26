package ru.mzuev.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для представления комментария.
 */
@Data
public class CommentDTO {

    /**
     * Уникальный идентификатор комментария.
     */
    private Long id;

    /**
     * Содержимое комментария (не более 1000 символов).
     */
    @NotBlank(message = "{validation.comment.content.required}")
    @Size(max = 1000, message = "{validation.comment.content.size}")
    private String content;

    /**
     * Идентификатор задачи, к которой относится комментарий (обязательное поле).
     */
    @NotNull(message = "{validation.comment.taskId.required}")
    @Positive(message = "{validation.comment.taskId.positive}")
    private Long taskId;

    /**
     * Идентификатор пользователя, создавшего комментарий.
     */
    private Long userId;
}