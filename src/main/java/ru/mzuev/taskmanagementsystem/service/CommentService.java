package ru.mzuev.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mzuev.taskmanagementsystem.dto.CommentDTO;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.mapper.CommentMapper;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.repository.CommentRepository;

/**
 * Сервис для работы с комментариями.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    /**
     * Создает комментарий для задачи.
     *
     * @param commentRequest Запрос с данными комментария.
     * @return Созданный комментарий в формате DTO.
     * @throws AccessDeniedException Если пользователь не является администратором или исполнителем задачи.
     */
    @Transactional
    public CommentDTO createComment(CommentRequest commentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.findEntityByEmail(email);

        Task task = taskService.getTaskEntityById(commentRequest.getTaskId());

        // Если пользователь не админ и не является исполнителем задачи, выбрасываем исключение
        if (!isAdmin(auth) && (task.getExecutor() == null || !currentUser.getEmail().equals(task.getExecutor().getEmail()))) {
            throw new AccessDeniedException("Пользователь может комментировать только свои задачи.");
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    /**
     * Возвращает комментарии для указанной задачи с пагинацией.
     *
     * @param taskId Идентификатор задачи.
     * @param pageable Параметры пагинации.
     * @return Страница комментариев в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByTask(Long taskId, Pageable pageable) {
        if (!taskService.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        Page<Comment> comments = commentRepository.findByTaskId(taskId, pageable);
        return comments.map(commentMapper::toDTO);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
    }
}