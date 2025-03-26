package ru.mzuev.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос для создания комментария.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    /**
     * Содержимое комментария (не более 1000 символов).
     */
    @NotBlank(message = "{validation.comment.content.required}")
    @Size(max = 1000, message = "{validation.comment.content.size}")
    private String content;

    /**
     * Идентификатор задачи, к которой относится комментарий.
     */
    private Long taskId;
}