package ru.otus.java.basic.user.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.basic.user.dto.UserResponse;
import ru.otus.java.basic.user.dto.UserUpdateRequest;
import ru.otus.java.basic.user.service.UserService;
import ru.otus.java.basic.core.security.JwtService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class UserController {
    private final UserService userService = new UserService();
    private final JwtService jwtService;

    public UserController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public UserResponse getMe(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return userService.getUserById(userId);
    }

    @PutMapping("/update")
    public UserResponse updateUserData(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserUpdateRequest request) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return userService.updateUserData(userId, request);
    }
}