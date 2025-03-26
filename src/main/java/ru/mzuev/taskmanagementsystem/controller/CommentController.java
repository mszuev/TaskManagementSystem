package ru.mzuev.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.dto.CommentDTO;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.service.CommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        CommentDTO createdCommentDTO = commentService.createComment(commentRequest);
        return ResponseEntity.ok(createdCommentDTO);
    }

    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        Page<CommentDTO> comments = commentService.getCommentsByTask(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}