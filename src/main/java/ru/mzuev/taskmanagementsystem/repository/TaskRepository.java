package ru.mzuev.taskmanagementsystem.repository;

import ru.mzuev.taskmanagementsystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с задачами. Поддерживает поиск по автору, исполнителю и проверку уникальности названия.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Возвращает страницу задач по автору.
     *
     * @param authorId Идентификатор автора.
     * @param pageable Параметры пагинации.
     * @return Страница задач.
     */
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * Возвращает страницу задач по исполнителю.
     *
     * @param executorId Идентификатор исполнителя.
     * @param pageable   Параметры пагинации.
     * @return Страница задач.
     */
    Page<Task> findByExecutorId(Long executorId, Pageable pageable);

    /**
     * Проверяет существование задачи с указанным названием.
     *
     * @param title Название задачи.
     * @return true, если задача существует, иначе false.
     */
    boolean existsByTitle(String title);
}