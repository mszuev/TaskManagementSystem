package ru.mzuev.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
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

/**
 * Сервис для управления задачами: создание, обновление, удаление, поиск.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    /**
     * Создает новую задачу.
     *
     * @param taskDTO DTO с данными задачи.
     * @return Созданная задача в формате DTO.
     * @throws TaskAlreadyExistsException Если задача с таким названием уже существует.
     */
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskRepository.existsByTitle(taskDTO.getTitle())) {
            throw new TaskAlreadyExistsException(taskDTO.getTitle());
        }
        Task task = taskMapper.toEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param taskId Идентификатор обновляемой задачи.
     * @param taskDTO DTO с новыми данными задачи.
     * @return Обновленная задача в формате DTO.
     * @throws TaskNotFoundException Если задача с указанным ID не найдена.
     */
    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        taskMapper.updateEntity(taskDTO, existingTask);
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDTO(updatedTask);
    }

    /**
     * Обновляет статус задачи.
     *
     * @param taskId Идентификатор задачи.
     * @param status Новый статус задачи.
     * @return Обновленная задача в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);
    }

    /**
     * Удаляет задачу по ID.
     *
     * @param taskId Идентификатор задачи.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskRepository.deleteById(taskId);
    }

    /**
     * Возвращает задачу по ID в формате DTO.
     *
     * @param taskId Идентификатор задачи.
     * @return Задача в формате DTO.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return taskMapper.toDTO(task);
    }

    /**
     * Возвращает сущность задачи по ID (для внутреннего использования).
     *
     * @param taskId Идентификатор задачи.
     * @return Сущность задачи.
     * @throws TaskNotFoundException Если задача не найдена.
     */
    @Transactional(readOnly = true)
    public Task getTaskEntityById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    /**
     * Возвращает страницу задач по автору.
     *
     * @param authorId Идентификатор автора.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница задач в формате DTO.
     * @throws UserNotFoundException Если автор не найден.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasksByAuthor(Long authorId, Pageable pageable) {
        if (!userService.existsById(authorId)) {
            throw new UserNotFoundException("Автор не найден с id " + authorId);
        }
        Page<Task> tasks = taskRepository.findByAuthorId(authorId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    /**
     * Возвращает страницу задач по исполнителю.
     *
     * @param executorId Идентификатор исполнителя.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница задач в формате DTO.
     * @throws UserNotFoundException Если исполнитель не найден.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasksByExecutor(Long executorId, Pageable pageable) {
        if (!userService.existsById(executorId)) {
            throw new UserNotFoundException("Исполнитель не найден с id " + executorId);
        }
        Page<Task> tasks = taskRepository.findByExecutorId(executorId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    /**
     * Проверяет существование задачи по ID.
     *
     * @param taskId Идентификатор задачи.
     * @return true, если задача существует, иначе false.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long taskId) {
        return taskRepository.existsById(taskId);
    }

    /**
     * Проверяет, является ли пользователь исполнителем задачи.
     *
     * @param taskId Идентификатор задачи.
     * @param email Email пользователя.
     * @return true, если пользователь является исполнителем.
     * @throws TaskNotFoundException Если задача не найдена.
     * @throws AccessDeniedException Если пользователь не исполнитель.
     */
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