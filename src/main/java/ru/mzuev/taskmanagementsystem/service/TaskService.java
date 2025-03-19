package ru.mzuev.taskmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskAlreadyExistsException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.mapper.TaskMapper;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskRepository.existsByTitle(taskDTO.getTitle())) {
            throw new TaskAlreadyExistsException(taskDTO.getTitle());
        }
        Task task = taskMapper.toEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        taskMapper.updateEntity(taskDTO, existingTask);
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDTO(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskRepository.deleteById(taskId);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return taskMapper.toDTO(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskEntityById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasksByAuthor(Long authorId, Pageable pageable) {
        if (!userService.existsById(authorId)) {
            throw new UserNotFoundException("Автор не найден с id " + authorId);
        }
        Page<Task> tasks = taskRepository.findByAuthorId(authorId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasksByExecutor(Long executorId, Pageable pageable) {
        if (!userService.existsById(executorId)) {
            throw new UserNotFoundException("Исполнитель не найден с id " + executorId);
        }
        Page<Task> tasks = taskRepository.findByExecutorId(executorId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long taskId) {
        return taskRepository.existsById(taskId);
    }

    @Transactional(readOnly = true)
    public boolean isExecutor(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (task.getExecutor() == null || !task.getExecutor().getEmail().equals(email)) {
            throw new AccessDeniedException("Пользователь не является исполнителем задачи");
        }
        return true;
    }
}