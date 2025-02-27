package ru.mzuev.taskmanagementsystem.controller;

import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.security.JwtUtil;
import ru.mzuev.taskmanagementsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // DTO для запросов регистрации и логина
    public static class AuthRequest {
        public String email;
        public String password;
    }

    // DTO для ответа логина
    public static class AuthResponse {
        public String email;
        public String token;

        public AuthResponse(String email, String token) {
            this.email = email;
            this.token = token;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        try {
            userService.registerUser(authRequest.email, authRequest.password);
            return ResponseEntity.ok("Пользователь зарегистрирован успешно");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // После успешной аутентификации генерируется JWT токен
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email, authRequest.password)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Неверные email или пароль");
        }
        User user = userService.findByEmail(authRequest.email);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(user.getEmail(), token));
    }
}
