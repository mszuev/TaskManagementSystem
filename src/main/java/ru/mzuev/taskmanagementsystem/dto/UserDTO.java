package ru.mzuev.taskmanagementsystem.dto;

import lombok.Data;

/**
 * DTO для представления пользователя. Содержит базовые данные пользователя.
 */
@Data
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Email пользователя. Уникален в системе.
     */
    private String email;

    /**
     * Роль пользователя (например, ROLE_ADMIN, ROLE_USER).
     */
    private String role;
}