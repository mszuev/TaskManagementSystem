package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.UserDTO;
import ru.mzuev.taskmanagementsystem.model.User;

/**
 * Маппер для преобразования между сущностью {@link User} и DTO {@link UserDTO}.
 */
@Component
public class UserMapper {

    /**
     * Преобразует сущность пользователя в DTO.
     *
     * @param user Сущность пользователя.
     * @return DTO пользователя.
     */
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    /**
     * Преобразует DTO пользователя в сущность (без пароля).
     *
     * @param userDTO DTO пользователя.
     * @return Сущность пользователя.
     */
    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        return user;
    }
}