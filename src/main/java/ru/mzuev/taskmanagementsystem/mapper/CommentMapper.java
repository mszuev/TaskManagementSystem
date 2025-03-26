package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.CommentDTO;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import ru.mzuev.taskmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Маппер для преобразования между сущностью {@link Comment} и DTO {@link CommentDTO}.
 */
@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final TaskService taskService;
    private final UserService userService;

    /**
     * Преобразует сущность комментария в DTO.
     *
     * @param comment Сущность комментария.
     * @return DTO комментария.
     */
    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setUserId(comment.getUser().getId());
        return commentDTO;
    }

    /**
     * Преобразует DTO комментария в сущность.
     *
     * @param commentDTO DTO комментария.
     * @return Сущность комментария.
     * @throws TaskNotFoundException Если задача не найдена.
     * @throws UserNotFoundException Если пользователь не найден.
     */
    public Comment toEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTask(taskService.getTaskEntityById(commentDTO.getTaskId()));
        comment.setUser(userService.findEntityById(commentDTO.getUserId()));
        return comment;
    }
}