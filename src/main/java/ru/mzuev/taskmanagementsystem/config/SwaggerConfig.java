package ru.mzuev.taskmanagementsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger/OpenAPI для генерации документации API.
 * Документация доступна по адресу: <code>/swagger-ui/index.html</code>.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Создает кастомную конфигурацию OpenAPI с описанием системы управления задачами.
     *
     * @return Объект OpenAPI с метаданными API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management System API")
                        .version("1.0")
                        .description("API для системы управления задачами с поддержкой JWT-аутентификации"));
    }
}