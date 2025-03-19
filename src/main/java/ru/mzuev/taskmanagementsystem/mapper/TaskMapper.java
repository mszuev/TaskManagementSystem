package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.UserService;

@Component
public class TaskMapper {

    private final UserService userService;

    @Autowired
    public TaskMapper(UserService userService) {
        this.userService = userService;
    }

    public TaskDTO toDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setAuthorId(task.getAuthor().getId());
        taskDTO.setExecutorId(task.getExecutor() != null ? task.getExecutor().getId() : null);
        return taskDTO;
    }

    public Task toEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setAuthor(userService.findById(taskDTO.getAuthorId()));
        if (taskDTO.getExecutorId() != null) {
            task.setExecutor(userService.findById(taskDTO.getExecutorId()));
        }
        return task;
    }

    public void updateEntity(TaskDTO taskDTO, Task task) {
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setAuthor(userService.findById(taskDTO.getAuthorId()));
        if (taskDTO.getExecutorId() != null) {
            task.setExecutor(userService.findById(taskDTO.getExecutorId()));
        }
    }
}