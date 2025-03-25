package ru.mzuev.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "{validation.task.title.required}")
    @Size(max = 200, message = "{validation.task.title.size}")
    private String title;

    @Size(max = 1000, message = "{validation.task.description.size}")
    private String description;

    @NotBlank(message = "{validation.task.status.required}")
    @Pattern(regexp = "в очереди|в работе|на проверке|завершена",
            message = "{validation.task.status.pattern}")
    private String status;

    @NotBlank(message = "{validation.task.priority.required}")
    @Pattern(regexp = "низкий|средний|высокий",
            message = "{validation.task.priority.pattern}")
    private String priority;

    @NotNull(message = "{validation.task.authorId.required}")
    private Long authorId;

    private Long executorId;
}