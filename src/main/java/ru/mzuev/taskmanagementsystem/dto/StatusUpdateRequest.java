package ru.mzuev.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на обновление статуса задачи. Содержит новый статус.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {

    /**
     * Новый статус задачи. Допустимые значения: "в очереди", "в работе", "на проверке", "завершена".
     */
    @NotBlank(message = "{validation.task.status.required}")
    @Pattern(regexp = "в очереди|в работе|на проверке|завершена",
            message = "{validation.task.status.pattern}")
    private String status;
}