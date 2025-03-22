package ru.mzuev.taskmanagementsystem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import ru.mzuev.taskmanagementsystem.dto.CommentRequest;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// Для сброса контекста перед каждым тестом
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;

    private String adminToken;
    private String executorToken;
    private String anotherUserToken;
    private Long adminId;
    private Long executorId;
    private Long createdTaskId;

    @BeforeEach
    void setUp() {
        // Регистрация и аутентификация админа
        AuthRequest adminRegisterRequest = new AuthRequest("admin@test.com", "adminPass");
        restTemplate.postForEntity("/api/auth/register", adminRegisterRequest, String.class);
        AuthResponse adminAuth = restTemplate.postForObject("/api/auth/login", adminRegisterRequest, AuthResponse.class);
        adminToken = "Bearer " + adminAuth.getToken();
        adminId = userService.findByEmail("admin@test.com").getId();

        // Регистрация и аутентификация исполнителя
        AuthRequest executorRegisterRequest = new AuthRequest("executor@test.com", "execPass");
        restTemplate.postForEntity("/api/auth/register", executorRegisterRequest, String.class);
        AuthResponse executorAuth = restTemplate.postForObject("/api/auth/login", executorRegisterRequest, AuthResponse.class);
        executorToken = "Bearer " + executorAuth.getToken();
        executorId = userService.findByEmail("executor@test.com").getId();

        // Регистрация и аутентификация НЕ исполнителя
        AuthRequest anotherUserRegisterRequest = new AuthRequest("anotheruser@test.com", "anotherPass");
        restTemplate.postForEntity("/api/auth/register", anotherUserRegisterRequest, String.class);
        AuthResponse anotherUserAuth = restTemplate.postForObject("/api/auth/login", anotherUserRegisterRequest, AuthResponse.class);
        anotherUserToken = "Bearer " + anotherUserAuth.getToken();

        // Создаем тестовую задачу
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus("в очереди");
        taskDTO.setPriority("высокий");
        taskDTO.setAuthorId(adminId);
        taskDTO.setExecutorId(executorId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);
        ResponseEntity<TaskDTO> response = restTemplate.postForEntity("/api/tasks", new HttpEntity<>(taskDTO, headers), TaskDTO.class);
        createdTaskId = response.getBody().getId();
    }

    @Test
    public void testCreateCommentByExecutor_Success() {
        CommentRequest commentRequest = new CommentRequest("Test Comment", createdTaskId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", executorToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CommentRequest> request = new HttpEntity<>(commentRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/comments", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Test Comment");
    }

    @Test
    public void testCreateComment_NonExecutorRole_ShouldReturnForbidden() {
        CommentRequest commentRequest = new CommentRequest("Unauthorized Comment", createdTaskId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", anotherUserToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CommentRequest> request = new HttpEntity<>(commentRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/comments", HttpMethod.POST, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Доступ запрещен");
    }
}