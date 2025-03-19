package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.CommentDTO;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import ru.mzuev.taskmanagementsystem.service.UserService;

@Component
public class CommentMapper {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public CommentMapper(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setUserId(comment.getUser().getId());
        return commentDTO;
    }

    public Comment toEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTask(taskService.getTaskEntityById(commentDTO.getTaskId()));
        comment.setUser(userService.findById(commentDTO.getUserId()));
        return comment;
    }
}