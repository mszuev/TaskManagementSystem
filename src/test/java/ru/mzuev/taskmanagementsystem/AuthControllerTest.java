package ru.mzuev.taskmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRegisterAndLogin_Success() {
        // Регистрация нового пользователя
        AuthRequest authRequest = new AuthRequest("testuser@example.com", "testPass123");
        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/auth/register", authRequest, String.class);

        // Проверяем успешную регистрацию
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Логин с теми же учетными данными
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", authRequest, AuthResponse.class);
        AuthResponse authResponse = loginResponse.getBody();

        // Проверяем успешный логин
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isNotBlank();
        assertThat(authResponse.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    public void testRegister_DuplicateEmail_Failure() {
        // Первая регистрация
        AuthRequest authRequest = new AuthRequest("duplicate@example.com", "testPass123");
        restTemplate.postForEntity("/api/auth/register", authRequest, String.class);

        // Попытка повторной регистрации с тем же email
        ResponseEntity<String> duplicateResponse = restTemplate.postForEntity("/api/auth/register", authRequest, String.class);

        // Проверяем ошибку
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(duplicateResponse.getBody()).contains("Пользователь с email duplicate@example.com уже существует");
    }

    @Test
    public void testLogin_InvalidCredentials_Failure() {
        // Регистрация пользователя
        AuthRequest authRequest = new AuthRequest("valid@example.com", "validPass");
        restTemplate.postForEntity("/api/auth/register", authRequest, String.class);

        // Попытка входа с неверным паролем
        AuthRequest invalidRequest = new AuthRequest("valid@example.com", "wrongPass");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/api/auth/login", invalidRequest, String.class);

        // Проверяем ошибку
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(loginResponse.getBody()).contains("Неверные email или пароль");
    }
}