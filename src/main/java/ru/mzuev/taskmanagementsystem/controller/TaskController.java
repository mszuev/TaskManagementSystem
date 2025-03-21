package ru.mzuev.taskmanagementsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.dto.StatusUpdateRequest;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTaskDTO = taskService.createTask(taskDTO);
        return ResponseEntity.ok(createdTaskDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTaskDTO = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isExecutor(#id, authentication.name)")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest statusUpdateRequest) {
        TaskDTO updatedTaskDTO = taskService.updateTaskStatus(id, statusUpdateRequest.getStatus());
        return ResponseEntity.ok(updatedTaskDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Задача удалена");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isExecutor(#id, authentication.name)")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/by-author")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskDTO>> getTasksByAuthor(@RequestParam Long authorId, Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTasksByAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-executor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskDTO>> getTasksByExecutor(@RequestParam Long executorId, Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTasksByExecutor(executorId, pageable);
        return ResponseEntity.ok(tasks);
    }
}