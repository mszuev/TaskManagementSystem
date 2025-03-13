package ru.mzuev.taskmanagementsystem.repository;

import ru.mzuev.taskmanagementsystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);
    Page<Task> findByExecutorId(Long executorId, Pageable pageable);
    boolean existsByTitle(String title);
}
