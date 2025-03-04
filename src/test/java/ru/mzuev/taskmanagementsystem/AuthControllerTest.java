package ru.mzuev.taskmanagementsystem;

import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRegisterAndLogin() {
        // Регистрируем пользователя
        AuthRequest authRequest = new AuthRequest("testuser@example.com", "testPass123");
        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/auth/register", authRequest, String.class);
        assertThat(registerResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // Повторная регистрация должна вернуть ошибку
        ResponseEntity<String> duplicateResponse = restTemplate.postForEntity("/api/auth/register", authRequest, String.class);
        assertThat(duplicateResponse.getStatusCode().is4xxClientError()).isTrue();

        // Логинимся
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", authRequest, AuthResponse.class);
        assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();
        AuthResponse authResponse = loginResponse.getBody();
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getEmail()).isEqualTo("testuser@example.com");
        assertThat(authResponse.getToken()).isNotBlank();
    }
}