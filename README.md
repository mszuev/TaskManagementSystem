# Task Management System

## Описание
Проект реализует REST API для управления задачами с поддержкой аутентификации и авторизации через JWT, ролевой системой (администратор и пользователь), CRUD для задач и возможность оставлять комментарии.

## Технологии
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Swagger (OpenAPI)
- Docker, Docker Compose

## Локальный запуск
1) склонировать проект локально 
2) собрать проект: mvn clean package -DskipTests
3) если не установлен докер - установить, открыть терминал в корневой директории проекта и выполнить docker-compose up --build

API будет доступен по адресу: http://localhost:8080
Документация Swagger UI: http://localhost:8080/swagger-ui/index.html

## Тестирование API через Postman или Swagger
Зарегистрируйте пользователей через POST /api/auth/register
Получите JWT-токены через POST /api/auth/login
Используйте полученный токен в заголовке Authorization для доступа к защищённым эндпоинтам.
Пример создания задачи: POST /api/tasks
Пример добавления комментария: POST /api/comments
