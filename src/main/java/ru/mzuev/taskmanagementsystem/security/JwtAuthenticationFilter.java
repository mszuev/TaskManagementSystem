package ru.mzuev.taskmanagementsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр, который перехватывает каждый HTTP-запрос, извлекает JWT-токен из заголовка Authorization,
 * проверяет его валидность и устанавливает аутентификацию в контекст Spring Security, если токен корректный
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Обрабатывает каждый HTTP-запрос для проверки JWT.
     *
     * @param request HTTP-запрос.
     * @param response HTTP-ответ.
     * @param filterChain Цепочка фильтров.
     * @throws ServletException В случае ошибок сервлета.
     * @throws IOException В случае ошибок ввода/вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Извлекаем заголовок Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Если заголовок начинается с "Bearer ", извлекаем сам токен
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                // Извлекаем email пользователя из токена
                email = jwtUtil.extractEmail(token);
            }
        }

        // Если email получен и аутентификация еще не установлена
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Загружаем пользователя из базы данных
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            // Создаем объект аутентификации
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Устанавливаем аутентификацию в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}