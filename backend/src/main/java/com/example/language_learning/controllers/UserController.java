package com.example.language_learning.controllers;

import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import com.example.language_learning.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/register")
    public UserDTO createUser(@Valid @RequestBody  CreateUserRequest request) {
        logger.info("Received request to create a user with username: {}", request.getUsername());
        return userService.createNewUser(request);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received request to login with username: {}", request.getUsername());
        return userService.login(request);
    }
}
