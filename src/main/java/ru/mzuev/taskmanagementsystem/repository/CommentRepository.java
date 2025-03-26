package ru.mzuev.taskmanagementsystem.repository;

import ru.mzuev.taskmanagementsystem.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с комментариями. Поддерживает пагинацию при поиске комментариев по задаче.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Возвращает страницу комментариев для указанной задачи.
     *
     * @param taskId   Идентификатор задачи.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница комментариев.
     */
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);
}