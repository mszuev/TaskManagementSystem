package ru.mzuev.taskmanagementsystem.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующей задаче.
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Создает исключение с сообщением об отсутствии задачи.
     *
     * @param taskId Идентификатор несуществующей задачи.
     */
    public TaskNotFoundException(Long taskId) {
        super("Задача не найдена с id " + taskId);
    }
}