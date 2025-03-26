package ru.mzuev.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.mzuev.taskmanagementsystem.dto.AuthRequest;
import ru.mzuev.taskmanagementsystem.dto.AuthResponse;
import ru.mzuev.taskmanagementsystem.dto.UserDTO;
import ru.mzuev.taskmanagementsystem.exception.InvalidCredentialsException;
import ru.mzuev.taskmanagementsystem.security.JwtUtil;
import ru.mzuev.taskmanagementsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody AuthRequest authRequest) {
        UserDTO userDTO = userService.registerUser(authRequest.getEmail(), authRequest.getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }
        UserDTO userDTO = userService.findDTOByEmail(authRequest.getEmail());
        String token = jwtUtil.generateToken(userDTO.getEmail(), userDTO.getRole());
        return ResponseEntity.ok(new AuthResponse(userDTO.getEmail(), token));
    }
}