package ru.mzuev.taskmanagementsystem.service;

import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskAlreadyExistsException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Task createTask(Task task) {
        // Проверяем, существует ли задача с таким же названием
        if (taskRepository.existsByTitle(task.getTitle())) {
            throw new TaskAlreadyExistsException(task.getTitle());
        }
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setAuthor(updatedTask.getAuthor());
        existingTask.setExecutor(updatedTask.getExecutor());
        return taskRepository.save(existingTask);
    }

    public Task updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskRepository.deleteById(taskId);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    public Page<Task> getTasksByAuthor(Long authorId, Pageable pageable) {
        // Проверяем, существует ли пользователь
        if (!userService.existsById(authorId)) {
            throw new UserNotFoundException("Автор не найден с id " + authorId);
        }
        return taskRepository.findByAuthorId(authorId, pageable);
    }

    public Page<Task> getTasksByExecutor(Long executorId, Pageable pageable) {
        // Проверяем, существует ли пользователь
        if (!userService.existsById(executorId)) {
            throw new UserNotFoundException("Исполнитель не найден с id " + executorId);
        }
        return taskRepository.findByExecutorId(executorId, pageable);
    }

    public boolean existsById(Long taskId) {
        return taskRepository.existsById(taskId);
    }

    public boolean isExecutor(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (task.getExecutor() == null || !task.getExecutor().getEmail().equals(email)) {
            throw new AccessDeniedException("Пользователь не является исполнителем задачи");
        }
        return true;
    }
}