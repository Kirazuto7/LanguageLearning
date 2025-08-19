package com.example.language_learning.controllers;

import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.dto.user.UserDTO;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import com.example.language_learning.security.AuthenticationResponse;
import com.example.language_learning.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody  CreateUserRequest request, HttpServletResponse servletResponse) {
        logger.info("Received request to create a user with username: {}", request.username());
        AuthenticationResponse authenticationResponse = userService.register(request);
        setJwtCookie(servletResponse, authenticationResponse.token());
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request, HttpServletResponse servletResponse) {
        logger.info("Received request to login with username: {}", request.username());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        AuthenticationResponse authenticationResponse = userService.login(request);
        setJwtCookie(servletResponse, authenticationResponse.token());
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/settings")
    public ResponseEntity<SettingsDTO> updateUserSettings(@RequestBody SettingsDTO updateRequest, Authentication authentication) {
        String username = authentication.getName();
        SettingsDTO updatedSettings = userService.updateSettings(username, updateRequest);
        return ResponseEntity.ok(updatedSettings);
    }

    private void setJwtCookie(HttpServletResponse servletResponse, String token) {
        Cookie cookie = new Cookie("jwt-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        servletResponse.addCookie(cookie);
    }
}
