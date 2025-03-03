package ru.mzuev.taskmanagementsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import ru.mzuev.taskmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final TaskService taskService;
    private final UserService userService;

    public CommentController(CommentService commentService, TaskService taskService, UserService userService) {
        this.commentService = commentService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.findByEmail(email);

        Task task = taskService.getTaskById(commentRequest.getTaskId());

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (task.getExecutor() == null || !currentUser.getEmail().equals(task.getExecutor().getEmail())) {
                return ResponseEntity.status(403)
                        .body("Пользователь может комментировать только свои задачи.");
            }
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);

        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByTask(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}