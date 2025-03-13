package ru.mzuev.taskmanagementsystem.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.repository.CommentRepository;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import ru.mzuev.taskmanagementsystem.service.UserService;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, TaskService taskService, UserService userService) {
        this.commentRepository = commentRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public Comment createComment(CommentRequest commentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.findByEmail(email);

        Task task = taskService.getTaskById(commentRequest.getTaskId());

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (task.getExecutor() == null || !currentUser.getEmail().equals(task.getExecutor().getEmail())) {
                throw new AccessDeniedException("Пользователь может комментировать только свои задачи.");
            }
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);

        return commentRepository.save(comment);
    }

    public Page<Comment> getCommentsByTask(Long taskId, Pageable pageable) {
        // Проверяем, существует ли задача
        if (!taskService.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        return commentRepository.findByTaskId(taskId, pageable);
    }
}