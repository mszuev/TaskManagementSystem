package ru.mzuev.taskmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Сущность задачи. Содержит данные о задаче, включая автора, исполнителя и комментарии.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {

    /**
     * Уникальный идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название задачи. Обязательное поле.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Описание задачи.
     */
    private String description;

    /**
     * Статус задачи (например, "в очереди", "в работе").
     */
    @Column(nullable = false)
    private String status;

    /**
     * Приоритет задачи (например, "низкий", "высокий").
     */
    @Column(nullable = false)
    private String priority;

    /**
     * Автор задачи. Связь с сущностью {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Исполнитель задачи. Связь с сущностью {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    /**
     * Список комментариев к задаче. Каскадное удаление при удалении задачи.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    public Task(String title, String description, String status, String priority, User author, User executor) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.executor = executor;
    }

    /**
     * Добавляет комментарий к задаче и устанавливает обратную связь.
     *
     * @param comment Комментарий для добавления.
     */
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTask(this);
    }

    /**
     * Удаляет комментарий из задачи и обрывает обратную связь.
     *
     * @param comment Комментарий для удаления.
     */
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTask(null);
    }
}