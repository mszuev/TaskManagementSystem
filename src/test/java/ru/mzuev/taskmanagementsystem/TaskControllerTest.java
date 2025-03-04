package ru.mzuev.taskmanagementsystem;

import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private final Long ADMIN_ID = 1L;

    // Устанавливаем id через рефлексию для доступа к сеттеру
    private void setUserId(User user, Long id) throws Exception {
        Method setIdMethod = User.class.getDeclaredMethod("setId", Long.class);
        setIdMethod.setAccessible(true);
        setIdMethod.invoke(user, id);
    }

    @BeforeEach
    public void setup() {
        // Регистрируем админа
        AuthRequest adminRequest = new AuthRequest("admin@example.com", "adminPass");
        restTemplate.postForEntity("/api/auth/register", adminRequest, String.class);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", adminRequest, AuthResponse.class);
        adminToken = loginResponse.getBody().getToken();
    }

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Testing task creation");
        task.setStatus("в ожидании");
        task.setPriority("высокий");

        // Устанавливаем автора и исполнителя как админа
        User adminUser = new User();
        setUserId(adminUser, ADMIN_ID);
        task.setAuthor(adminUser);
        task.setExecutor(adminUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Task> entity = new HttpEntity<>(task, headers);

        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/api/tasks", entity, Task.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Task createdTask = createResponse.getBody();
        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo("Test Task");

        // Получаем задачу по id
        ResponseEntity<Task> getResponse = restTemplate.exchange("/api/tasks/" + createdTask.getId(),
                HttpMethod.GET, new HttpEntity<>(headers), Task.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Task fetchedTask = getResponse.getBody();
        assertThat(fetchedTask).isNotNull();
        assertThat(fetchedTask.getTitle()).isEqualTo("Test Task");
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {
        Task task = new Task();
        task.setTitle("Status Task");
        task.setDescription("Task to update status");
        task.setStatus("в ожидании");
        task.setPriority("низкий");

        User adminUser = new User();
        setUserId(adminUser, ADMIN_ID);
        task.setAuthor(adminUser);
        task.setExecutor(adminUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Task> entity = new HttpEntity<>(task, headers);

        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/api/tasks", entity, Task.class);
        Task createdTask = createResponse.getBody();
        assertThat(createdTask).isNotNull();

        // Обновляем статус задачи
        String updateStatusJson = "{ \"status\": \"завершено\" }";
        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.set("Authorization", "Bearer " + adminToken);
        updateHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> updateEntity = new HttpEntity<>(updateStatusJson, updateHeaders);

        ResponseEntity<Task> updateResponse = restTemplate.exchange(
                "/api/tasks/" + createdTask.getId() + "/status",
                HttpMethod.PUT,
                updateEntity,
                Task.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Task updatedTask = updateResponse.getBody();
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getStatus()).isEqualTo("завершено");
    }
}