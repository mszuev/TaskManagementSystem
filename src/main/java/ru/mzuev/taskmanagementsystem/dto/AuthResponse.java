package ru.mzuev.taskmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ на успешную аутентификацию. Содержит email пользователя и JWT-токен.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Email аутентифицированного пользователя.
     */
    private String email;

    /**
     * JWT-токен для доступа к защищенным ресурсам.
     */
    private String token;
}