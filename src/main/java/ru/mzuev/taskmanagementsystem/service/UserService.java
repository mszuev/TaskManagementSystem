package ru.mzuev.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mzuev.taskmanagementsystem.dto.UserDTO;
import ru.mzuev.taskmanagementsystem.exception.UserAlreadyExistsException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.mapper.UserMapper;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.repository.UserRepository;

/**
 * Сервис для управления пользователями: регистрация, поиск, проверка существования.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Регистрирует нового пользователя. Первый зарегистрированный пользователь получает роль ADMIN.
     *
     * @param email    Email пользователя.
     * @param password Пароль пользователя (хешируется перед сохранением).
     * @return Зарегистрированный пользователь в формате DTO.
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует.
     */
    @Transactional
    public UserDTO registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        String role = (userRepository.count() == 0) ? "ROLE_ADMIN" : "ROLE_USER";
        User user = new User(email, passwordEncoder.encode(password), role);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    /**
     * Находит пользователя по идентификатору и возвращает его в формате DTO.
     *
     * @param userId Идентификатор пользователя.
     * @return Пользователь в формате DTO.
     * @throws UserNotFoundException Если пользователь не найден.
     */
    // Методы для работы с DTO (для контроллеров)
    @Transactional(readOnly = true)
    public UserDTO findDTOById(Long userId) {
        User user = findEntityById(userId);
        return userMapper.toDTO(user);
    }

    /**
     * Находит пользователя по email и возвращает его в формате DTO.
     *
     * @param email Email пользователя.
     * @return Пользователь в формате DTO.
     * @throws UserNotFoundException Если пользователь не найден.
     */
    @Transactional(readOnly = true)
    public UserDTO findDTOByEmail(String email) {
        User user = findEntityByEmail(email);
        return userMapper.toDTO(user);
    }

    /**
     * Находит пользователя по идентификатору и возвращает его как сущность.
     *
     * @param userId Идентификатор пользователя.
     * @return Сущность пользователя.
     * @throws UserNotFoundException Если пользователь не найден.
     */
    // Методы для работы с сущностями (для внутреннего использования)
    @Transactional(readOnly = true)
    public User findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Находит пользователя по email и возвращает его как сущность.
     *
     * @param email Email пользователя.
     * @return Сущность пользователя.
     * @throws UserNotFoundException Если пользователь не найден.
     */
    @Transactional(readOnly = true)
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return true, если пользователь существует, иначе false.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}