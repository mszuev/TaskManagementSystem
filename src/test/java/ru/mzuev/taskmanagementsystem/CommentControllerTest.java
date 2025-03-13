package ru.mzuev.taskmanagementsystem;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.model.Comment;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private String userToken;
    private Long taskId;

    private final Long ADMIN_ID = 1L;
    private final Long EXECUTOR_ID = 2L;

    // Устанавливаем id через рефлексию для доступа к сеттеру
    private void setUserId(User user, Long id) throws Exception {
        Method setIdMethod = User.class.getDeclaredMethod("setId", Long.class);
        setIdMethod.setAccessible(true);
        setIdMethod.invoke(user, id);
    }

    @BeforeEach
    public void setup() throws Exception {
        // Регистрируем администратора (первый зареганый пользователь становится админом)
        AuthRequest adminRequest = new AuthRequest("admin@example.com", "adminPass");
        restTemplate.postForEntity("/api/auth/register", adminRequest, String.class);
        ResponseEntity<AuthResponse> adminLoginResponse = restTemplate.postForEntity("/api/auth/login", adminRequest, AuthResponse.class);
        adminToken = adminLoginResponse.getBody().getToken();

        // Регистрируем обычного юзера-исполнителя
        AuthRequest userRequest = new AuthRequest("user@example.com", "userPass");
        restTemplate.postForEntity("/api/auth/register", userRequest, String.class);
        ResponseEntity<AuthResponse> userLoginResponse = restTemplate.postForEntity("/api/auth/login", userRequest, AuthResponse.class);
        userToken = userLoginResponse.getBody().getToken();

        // Создаем задачу, назначая автором админа и исполнителем юзера
        Task task = new Task();
        task.setTitle("Task for Comment");
        task.setDescription("Task to test comments");
        task.setStatus("в ожидании");
        task.setPriority("средний");

        User adminUser = new User();
        setUserId(adminUser, ADMIN_ID);
        task.setAuthor(adminUser);

        User executor = new User();
        setUserId(executor, EXECUTOR_ID);
        task.setExecutor(executor);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Task> taskEntity = new HttpEntity<>(task, headers);
        ResponseEntity<Task> taskResponse = restTemplate.postForEntity("/api/tasks", taskEntity, Task.class);
        taskId = taskResponse.getBody().getId();
    }

    @Test
    public void testCreateCommentByExecutor() {
        // Попытка создать комментарий от имени юзера-исполнителя
        CommentRequest commentRequest = new CommentRequest("Comment from executor", taskId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(commentRequest, headers);

        ResponseEntity<Comment> response = restTemplate.postForEntity("/api/comments", entity, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Comment createdComment = response.getBody();
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getContent()).isEqualTo("Comment from executor");
    }

    @Test
    public void testCreateCommentByNonExecutor() {
        // Регистрируем еще одного юзера, который не является исполнителем задачи
        AuthRequest otherUserRequest = new AuthRequest("other@example.com", "otherPass");
        restTemplate.postForEntity("/api/auth/register", otherUserRequest, String.class);
        ResponseEntity<AuthResponse> otherLoginResponse = restTemplate.postForEntity("/api/auth/login", otherUserRequest, AuthResponse.class);
        String otherUserToken = otherLoginResponse.getBody().getToken();

        CommentRequest commentRequest = new CommentRequest("Comment from non-executor", taskId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + otherUserToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(commentRequest, headers);

        // Выполняем запрос
        ResponseEntity<String> response = restTemplate.postForEntity("/api/comments", entity, String.class);

        // Проверяем статус ответа
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Проверяем тело ответа
        assertThat(response.getBody()).contains("Пользователь может комментировать только свои задачи.");
    }
}