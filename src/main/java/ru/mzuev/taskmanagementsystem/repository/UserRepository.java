package ru.mzuev.taskmanagementsystem.repository;

import ru.mzuev.taskmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями. Поддерживает поиск по email.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по email.
     *
     * @param email Email пользователя.
     * @return Optional с пользователем, если найден.
     */
    Optional<User> findByEmail(String email);
}