package ru.mzuev.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.dto.CommentDTO;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.service.CommentService;
import lombok.RequiredArgsConstructor;

/**
 * Контроллер для управления комментариями к задачам.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Создает новый комментарий к задаче.
     *
     * @param commentRequest Запрос с содержимым комментария и идентификатором задачи.
     * @return Созданный комментарий в формате DTO.
     * @throws AccessDeniedException Если пользователь не имеет прав на создание комментария.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        CommentDTO createdCommentDTO = commentService.createComment(commentRequest);
        return ResponseEntity.ok(createdCommentDTO);
    }

    /**
     * Возвращает страницу комментариев для указанной задачи.
     *
     * @param taskId Идентификатор задачи.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница комментариев в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        Page<CommentDTO> comments = commentService.getCommentsByTask(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}