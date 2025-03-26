package ru.mzuev.taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Сущность пользователя системы. Хранит данные для аутентификации и авторизации.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email пользователя. Уникален и обязателен для заполнения.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Хешированный пароль пользователя. Обязателен для заполнения.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Роль пользователя в системе (например, ROLE_ADMIN, ROLE_USER).
     */
    @Column(nullable = false)
    private String role;

    /**
     * Создает пользователя с указанными email, паролем и ролью.
     *
     * @param email Email пользователя.
     * @param password Пароль пользователя (открытый текст, будет захеширован).
     * @param role Роль пользователя.
     */
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}