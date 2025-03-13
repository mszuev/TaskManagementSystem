package ru.mzuev.taskmanagementsystem.service;

import ru.mzuev.taskmanagementsystem.exception.UserAlreadyExistsException;
import ru.mzuev.taskmanagementsystem.exception.UserNotFoundException;
import ru.mzuev.taskmanagementsystem.model.User;
import ru.mzuev.taskmanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        // Если в системе нет ни одного пользователя, назначаем роль админа
        String role = (userRepository.count() == 0) ? "ROLE_ADMIN" : "ROLE_USER";
        User user = new User(email, passwordEncoder.encode(password), role);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}