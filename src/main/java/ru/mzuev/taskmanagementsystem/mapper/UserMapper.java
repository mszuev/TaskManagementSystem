package ru.mzuev.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.dto.UserDTO;
import ru.mzuev.taskmanagementsystem.model.User;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        return user;
    }
}