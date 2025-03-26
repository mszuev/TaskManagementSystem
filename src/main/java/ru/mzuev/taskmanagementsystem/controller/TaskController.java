package ru.mzuev.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.dto.StatusUpdateRequest;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.exception.AccessDeniedException;
import ru.mzuev.taskmanagementsystem.exception.TaskAlreadyExistsException;
import ru.mzuev.taskmanagementsystem.exception.TaskNotFoundException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;

/**
 * Контроллер для управления задачами. Обеспечивает создание, обновление, удаление и поиск задач.
 * Доступ к методам регулируется ролями пользователей и проверкой прав.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Создает новую задачу. Доступно только пользователям с ролью ADMIN.
     *
     * @param taskDTO DTO задачи с данными для создания.
     * @return Созданная задача в формате DTO.
     * @throws TaskAlreadyExistsException Если задача с таким названием уже существует.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTaskDTO = taskService.createTask(taskDTO);
        return ResponseEntity.ok(createdTaskDTO);
    }

    /**
     * Обновляет существующую задачу. Доступно только пользователям с ролью ADMIN.
     *
     * @param id      Идентификатор задачи.
     * @param taskDTO DTO с обновленными данными задачи.
     * @return Обновленная задача в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTaskDTO = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    /**
     * Обновляет статус задачи. Доступно администраторам или исполнителю задачи.
     *
     * @param id Идентификатор задачи.
     * @param statusUpdateRequest Запрос с новым статусом.
     * @return Обновленная задача в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     * @throws AccessDeniedException Если пользователь не имеет прав на изменение статуса.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isExecutor(#id, authentication.name)")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest statusUpdateRequest) {
        TaskDTO updatedTaskDTO = taskService.updateTaskStatus(id, statusUpdateRequest.getStatus());
        return ResponseEntity.ok(updatedTaskDTO);
    }

    /**
     * Удаляет задачу. Доступно только пользователям с ролью ADMIN.
     *
     * @param id Идентификатор задачи.
     * @return Сообщение об успешном удалении.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Задача удалена");
    }

    /**
     * Возвращает задачу по идентификатору. Доступно администраторам или исполнителю задачи.
     *
     * @param id Идентификатор задачи.
     * @return Задача в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isExecutor(#id, authentication.name)")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    /**
     * Возвращает задачи по автору. Доступно только администраторам.
     *
     * @param authorId Идентификатор автора.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница задач в формате DTO.
     * @throws UserNotFoundException Если автор не найден.
     */
    @GetMapping("/by-author")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskDTO>> getTasksByAuthor(@RequestParam Long authorId, Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTasksByAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Возвращает задачи по исполнителю. Доступно только администраторам.
     *
     * @param executorId Идентификатор исполнителя.
     * @param pageable   Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница задач в формате DTO.
     * @throws UserNotFoundException Если исполнитель не найден.
     */
    @GetMapping("/by-executor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskDTO>> getTasksByExecutor(@RequestParam Long executorId, Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTasksByExecutor(executorId, pageable);
        return ResponseEntity.ok(tasks);
    }
}