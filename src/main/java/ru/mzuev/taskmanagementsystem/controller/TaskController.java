package ru.mzuev.taskmanagementsystem.controller;

import ru.mzuev.taskmanagementsystem.dto.StatusUpdateRequest;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isExecutor(#id)")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id,
                                              @RequestBody StatusUpdateRequest statusUpdateRequest) {
        try {
            Task updatedTask = taskService.updateTaskStatus(id, statusUpdateRequest.getStatus());
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Задача удалена");
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isExecutor(#id)")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-author")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTasksByAuthor(@RequestParam Long authorId, Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-executor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTasksByExecutor(@RequestParam Long executorId, Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByExecutor(executorId, pageable);
        return ResponseEntity.ok(tasks);
    }
}
