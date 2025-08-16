package com.example.language_learning.controllers;

import com.example.language_learning.dto.SettingsDTO;
import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.entity.Settings;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import com.example.language_learning.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody  CreateUserRequest request) {
        logger.info("Received request to create a user with username: {}", request.getUsername());
        UserDTO newUser = userService.createNewUser(request);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received request to login with username: {}", request.getUsername());
        UserDTO user = userService.login(request);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/settings")
    public ResponseEntity<SettingsDTO> updateUserSettings(@PathVariable Long userId, @RequestBody SettingsDTO updateRequest) {
        SettingsDTO updatedSettings = userService.updateSettings(userId, updateRequest);
        return ResponseEntity.ok(updatedSettings);
    }
}
