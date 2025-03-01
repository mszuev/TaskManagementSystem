package ru.mzuev.taskmanagementsystem.service;

import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена с id " + taskId));
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
                .orElseThrow(() -> new RuntimeException("Задача не найдена с id " + taskId));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Задача не найдена с id " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена с id " + taskId));
    }

    public Page<Task> getTasksByAuthor(Long authorId, Pageable pageable) {
        return taskRepository.findByAuthorId(authorId, pageable);
    }

    public Page<Task> getTasksByExecutor(Long executorId, Pageable pageable) {
        return taskRepository.findByExecutorId(executorId, pageable);
    }
}