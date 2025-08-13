package com.example.language_learning.controllers;

import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import com.example.language_learning.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/register")
    public Mono<UserDTO> createUser(CreateUserRequest request) {
        logger.info("Received request to create a user with username: {}", request.getUsername());
        return userService.createNewUser(request);
    }

    @PostMapping("/login")
    public Mono<UserDTO> login(LoginRequest request) {
        logger.info("Received request to login with username: {}", request.getUsername());
        return userService.login(request);
    }
}
