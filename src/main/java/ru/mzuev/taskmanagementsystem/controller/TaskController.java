package ru.mzuev.taskmanagementsystem.controller;

import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// Доступ к эндпоинтам требует аутентификации JWT
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Задача удалена");
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    // Поиск задачи по автору
    @GetMapping("/by-author")
    public ResponseEntity<?> getTasksByAuthor(@RequestParam Long authorId, Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    // по исполнителю
    @GetMapping("/by-executor")
    public ResponseEntity<?> getTasksByExecutor(@RequestParam Long executorId, Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByExecutor(executorId, pageable);
        return ResponseEntity.ok(tasks);
    }
}
