package ru.mzuev.taskmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Сущность комментария. Содержит текст комментария, ссылки на задачу и пользователя.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Содержимое комментария. Обязательное поле, не более 1000 символов.
     */
    @Column(nullable = false)
    private String content;

    /**
     * Дата и время создания комментария. Устанавливается автоматически перед сохранением.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Задача, к которой относится комментарий. Связь с сущностью {@link Task}.
     */
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

    /**
     * Автор комментария. Связь с сущностью {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Создает комментарий с указанным содержимым, задачей и автором.
     *
     * @param content    Содержимое комментария.
     * @param createdAt  Дата создания.
     * @param task       Связанная задача.
     * @param user       Автор комментария.
     */
    public Comment(String content, LocalDateTime createdAt, Task task, User user) {
        this.content = content;
        this.createdAt = createdAt;
        this.task = task;
        this.user = user;
    }

    /**
     * Автоматически устанавливает дату создания комментария перед сохранением в БД.
     */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}