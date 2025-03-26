package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Маппер для преобразования между сущностью {@link Task} и DTO {@link TaskDTO}.
 */
@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserService userService;

    /**
     * Преобразует сущность задачи в DTO.
     *
     * @param task Сущность задачи.
     * @return DTO задачи.
     */
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

    /**
     * Преобразует DTO задачи в сущность.
     *
     * @param taskDTO DTO задачи.
     * @return Сущность задачи.
     * @throws UserNotFoundException Если автор или исполнитель не найдены.
     */
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

    /**
     * Обновляет существующую сущность задачи данными из DTO.
     *
     * @param taskDTO DTO с обновленными данными.
     * @param task Сущность задачи для обновления.
     * @throws UserNotFoundException Если автор или исполнитель не найдены.
     */
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