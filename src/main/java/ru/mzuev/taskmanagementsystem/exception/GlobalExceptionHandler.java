package ru.mzuev.taskmanagementsystem.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений. Преобразует исключения в структурированные ответы API
 * с использованием {@link ProblemDetail}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибки валидации полей DTO.
     *
     * @param ex Исключение {@link MethodArgumentNotValidException}.
     * @return Ответ с HTTP-статусом 400, содержащий детализацию ошибок валидации.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации данных"
        );

        // Собираем ошибки в формате { "field": "message" }
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage // Берём сообщение из messages.properties
                ));

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    /**
     * Обрабатывает отсутствие задачи.
     *
     * @param ex Исключение {@link TaskNotFoundException}.
     * @return Ответ с HTTP-статусом 404 и сообщением об ошибке.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFound(TaskNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Задача не найдена");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает конфликт при создании дублирующей задачи.
     *
     * @param ex Исключение {@link TaskAlreadyExistsException}.
     * @return Ответ с HTTP-статусом 409 и сообщением об ошибке.
     */
    @ExceptionHandler(TaskAlreadyExistsException.class)
    public ProblemDetail handleTaskAlreadyExists(TaskAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Конфликт данных");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает отказ в доступе к ресурсу.
     *
     * @param ex Исключение {@link AccessDeniedException}.
     * @return Ответ с HTTP-статусом 403 и сообщением об ошибке.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        problemDetail.setTitle("Доступ запрещен");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает ошибки авторизации Spring Security (например, срабатывание @PreAuthorize).
     *
     * @param ex Исключение {@link AuthorizationDeniedException}.
     * @return Ответ с HTTP-статусом 403 и сообщением об ошибке.
     */
    // для @PreAuthorize
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        problemDetail.setTitle("Ошибка доступа");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает конфликт при регистрации пользователя с существующим email.
     *
     * @param ex Исключение {@link UserAlreadyExistsException}.
     * @return Ответ с HTTP-статусом 409 и email конфликтующего пользователя.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Конфликт данных");
        problemDetail.setProperty("email", ex.getEmail());
        return problemDetail;
    }

    /**
     * Обрабатывает отсутствие пользователя.
     *
     * @param ex Исключение {@link UserNotFoundException}.
     * @return Ответ с HTTP-статусом 404 и сообщением об ошибке.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Пользователь не найден");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает отсутствие комментария.
     *
     * @param ex Исключение {@link CommentNotFoundException}.
     * @return Ответ с HTTP-статусом 404 и идентификатором комментария.
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ProblemDetail handleCommentNotFound(CommentNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Комментарий не найден");
        problemDetail.setProperty("commentId", ex.getCommentId());
        return problemDetail;
    }

    /**
     * Обрабатывает неверные учетные данные при аутентификации.
     *
     * @param ex Исключение {@link InvalidCredentialsException}.
     * @return Ответ с HTTP-статусом 401 и сообщением об ошибке.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problemDetail.setTitle("Ошибка аутентификации");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Обрабатывает все непредвиденные исключения.
     *
     * @param ex Исключение {@link Exception}.
     * @return Ответ с HTTP-статусом 500 и общим сообщением об ошибке.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера"
        );
        problemDetail.setTitle("Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}