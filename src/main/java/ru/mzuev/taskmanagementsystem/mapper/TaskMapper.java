package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserService userService;

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
        task.setAuthor(userService.findEntityById(taskDTO.getAuthorId()));
        if (taskDTO.getExecutorId() != null) {
            task.setExecutor(userService.findEntityById(taskDTO.getExecutorId()));
        }
        return task;
    }

    public void updateEntity(TaskDTO taskDTO, Task task) {
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setAuthor(userService.findEntityById(taskDTO.getAuthorId()));
        if (taskDTO.getExecutorId() != null) {
            task.setExecutor(userService.findEntityById(taskDTO.getExecutorId()));
        }
    }
}