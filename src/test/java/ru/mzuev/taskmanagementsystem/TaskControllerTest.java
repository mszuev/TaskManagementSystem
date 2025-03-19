package ru.mzuev.taskmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import ru.mzuev.taskmanagementsystem.dto.StatusUpdateRequest;
import ru.mzuev.taskmanagementsystem.dto.TaskDTO;
import ru.mzuev.taskmanagementsystem.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;
    private String adminToken;
    private String executorToken;
    private String anotheUserToken;
    private Long adminId;
    private Long executorId;
    private Long createdTaskId;

    @BeforeEach
    void setup() {
        // Регистрация админа
        AuthRequest adminRegisterRequest = new AuthRequest("admin@test.com", "adminPass");
        restTemplate.postForEntity("/api/auth/register", adminRegisterRequest, String.class);
        // Аутентифицируем админа
        AuthResponse adminAuth = restTemplate.postForObject("/api/auth/login", adminRegisterRequest, AuthResponse.class);
        adminToken = "Bearer " + adminAuth.getToken();
        adminId = userService.findByEmail("admin@test.com").getId();

        // Регистрация исполнителя
        AuthRequest executorRegisterRequest = new AuthRequest("executor@test.com", "execPass");
        restTemplate.postForEntity("/api/auth/register", executorRegisterRequest, String.class);
        // Аутентифицируем исполнителя
        AuthResponse executorAuth = restTemplate.postForObject("/api/auth/login", executorRegisterRequest, AuthResponse.class);
        executorToken = "Bearer " + executorAuth.getToken();
        executorId = userService.findByEmail("executor@test.com").getId();

        // Регистрация НЕ исполнителя
        AuthRequest anotherUserRegisterRequest = new AuthRequest("anotheruser@test.com", "anotherPass");
        restTemplate.postForEntity("/api/auth/register", anotherUserRegisterRequest, String.class);
        // Аутентифицируем НЕ исполнителя
        AuthResponse anotherUserAuth = restTemplate.postForObject("/api/auth/login", anotherUserRegisterRequest, AuthResponse.class);
        anotheUserToken = "Bearer " + anotherUserAuth.getToken();

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

        ResponseEntity<TaskDTO> response = restTemplate.postForEntity(
                "/api/tasks",
                new HttpEntity<>(taskDTO, headers),
                TaskDTO.class
        );

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            System.err.println("Ошибка при создании тестовой задачи: " + response.getStatusCode());
            System.err.println(response.getBody());
        }
        createdTaskId = response.getBody().getId();
    }

    @Test
    void createTask_AdminRole_ShouldCreateTask() {
        TaskDTO newTask = new TaskDTO();
        newTask.setTitle("New Task");
        newTask.setDescription("New Task Description");
        newTask.setStatus("в очереди");
        newTask.setPriority("высокий");
        newTask.setAuthorId(adminId);
        newTask.setExecutorId(executorId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);

        ResponseEntity<TaskDTO> response = restTemplate.postForEntity(
                "/api/tasks",
                new HttpEntity<>(newTask, headers),
                TaskDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("New Task");
    }

    @Test
    void createTask_NonAdminRole_ShouldReturnForbidden() {
        TaskDTO newTask = new TaskDTO();
        newTask.setTitle("Unauthorized Task");
        newTask.setDescription("Unauthorized Task Description");
        newTask.setStatus("в очереди");
        newTask.setPriority("высокий");
        newTask.setAuthorId(adminId);
        newTask.setExecutorId(executorId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", executorToken); // Токен юзера

        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/tasks",
                HttpMethod.POST,
                new HttpEntity<>(newTask, headers),
                Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateStatus_ExecutorRole_ShouldUpdateStatus() {
        StatusUpdateRequest statusUpdate = new StatusUpdateRequest("IN_PROGRESS");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", executorToken); // Токен исполнителя
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<TaskDTO> response = restTemplate.exchange(
                "/api/tasks/" + createdTaskId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusUpdate, headers),
                TaskDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void updateStatus_NonExecutorRole_ShouldReturnForbidden() {
        StatusUpdateRequest statusUpdate = new StatusUpdateRequest("IN_PROGRESS");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", anotheUserToken); // Токен другого юзера (не исполнителя)
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/tasks/" + createdTaskId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusUpdate, headers),
                Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}