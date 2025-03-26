package ru.mzuev.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для представления задачи. Содержит данные для создания, обновления и отображения задач.
 */
@Data
public class TaskDTO {

    /**
     * Уникальный идентификатор задачи. Генерируется автоматически и не указывается при создании.
     */
    @Schema(hidden = true)
    private Long id;

    /**
     * Название задачи. Обязательное поле, максимальная длина — 200 символов.
     */
    @NotBlank(message = "{validation.task.title.required}")
    @Size(max = 200, message = "{validation.task.title.size}")
    private String title;

    /**
     * Описание задачи. Максимальная длина — 1000 символов.
     */
    @Size(max = 1000, message = "{validation.task.description.size}")
    private String description;

    /**
     * Статус задачи. Допустимые значения: "в очереди", "в работе", "на проверке", "завершена".
     */
    @NotBlank(message = "{validation.task.status.required}")
    @Pattern(regexp = "в очереди|в работе|на проверке|завершена",
            message = "{validation.task.status.pattern}")
    private String status;

    /**
     * Приоритет задачи. Допустимые значения: "низкий", "средний", "высокий".
     */
    @NotBlank(message = "{validation.task.priority.required}")
    @Pattern(regexp = "низкий|средний|высокий",
            message = "{validation.task.priority.pattern}")
    private String priority;

    /**
     * Идентификатор автора задачи. Обязательное поле.
     */
    @NotNull(message = "{validation.task.authorId.required}")
    private Long authorId;

    /**
     * Идентификатор исполнителя задачи. Может быть null, если исполнитель не назначен.
     */
    private Long executorId;
}