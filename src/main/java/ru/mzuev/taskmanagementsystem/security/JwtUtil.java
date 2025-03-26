package ru.mzuev.taskmanagementsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Утилита для работы с JWT-токенами: генерация, валидация, извлечение данных.
 */
@Component
public class JwtUtil {

    // Секретный ключ для подписи JWT
    @Value("${jwt.secret}")
    private String secret;

    // Время жизни токена в миллисекундах
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Генерирует JWT-токен для пользователя.
     *
     * @param email Email пользователя.
     * @param role Роль пользователя.
     * @return Сгенерированный токен.
     */
    public String generateToken(String email, String role) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Извлекает email из токена.
     *
     * @param token JWT-токен.
     * @return Email пользователя.
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяет валидность токена.
     *
     * @param token JWT-токен.
     * @return true, если токен валиден, иначе false.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Позже добавить сюда логгирование
            return false;
        }
    }
}