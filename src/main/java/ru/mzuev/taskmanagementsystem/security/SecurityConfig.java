package ru.mzuev.taskmanagementsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Конфигурация безопасности Spring. Настраивает CORS, JWT-аутентификацию, права доступа к эндпоинтам.
 * Конфигурация Spring Security:
 * - Включает CORS
 * - Отключает CSRF и сессионное хранение (stateless)
 * - Настраивает публичный доступ к эндпоинтам для аутентификации и Swagger
 * - Все остальные запросы требуют аутентификации
 */
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Конфигурирует цепочку фильтров безопасности.
     *
     * @param http Объект конфигурации HTTP-безопасности.
     * @return Настроенная цепочка фильтров.
     * @throws Exception В случае ошибки конфигурации.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Включаем CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Отключаем CSRF, так как API является stateless
                .csrf(AbstractHttpConfigurer::disable)
                // Отключаем использование сессий
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Настройка авторизации для эндпоинтов
                .authorizeHttpRequests(auth -> auth
                        // Доступ к эндпоинтам для аутентификации и документации Swagger не требует авторизации
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Остальные эндпоинты доступны только аутентифицированным пользователям
                        .anyRequest().authenticated()
                );

        // Добавляем JWT-фильтр до стандартного фильтра аутентификации
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Настраивает CORS для разрешения запросов с определенных источников.
     *
     * @return Конфигурация CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Разрешенные источники (frontend)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://blablabla.com"
        ));

        // Разрешенные HTTP-методы
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Разрешенные заголовки
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        // Разрешить отправку cookie и авторизационных данных
        config.setAllowCredentials(true);

        // Время кеширования предварительных запросов
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Предоставляет кодировщик паролей (BCrypt).
     *
     * @return Экземпляр BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Предоставляет менеджер аутентификации.
     *
     * @param authConfig Конфигурация аутентификации.
     * @return Менеджер аутентификации.
     * @throws Exception В случае ошибки.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Настраивает источник сообщений для локализации.
     *
     * @return Источник сообщений.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}