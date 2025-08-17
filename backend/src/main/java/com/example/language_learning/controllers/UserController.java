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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody  CreateUserRequest request) {
        logger.info("Received request to create a user with username: {}", request.getUsername());
        userService.createNewUser(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDTO newUser = userService.getUserByUsername(request.getUsername());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received request to login with username: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDTO userDto = userService.getUserByUsername(request.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/settings")
    public ResponseEntity<?> updateUserSettings(@RequestBody SettingsDTO updateRequest, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated. If not, the principal will be a string "anonymousUser".
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        String username = auth.getName();
        SettingsDTO updatedSettings = userService.updateSettings(username, updateRequest);
        return ResponseEntity.ok(updatedSettings);
    }
}
