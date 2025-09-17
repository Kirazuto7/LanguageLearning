package com.example.language_learning.user;

import com.example.language_learning.user.dtos.SettingsDTO;
import com.example.language_learning.user.dtos.UserDTO;
import com.example.language_learning.user.data.User;
import com.example.language_learning.shared.requests.CreateUserRequest;
import com.example.language_learning.shared.requests.LoginRequest;
import com.example.language_learning.security.AuthenticationResponse;
import com.example.language_learning.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody  CreateUserRequest request, HttpServletResponse servletResponse) {
        log.info("Received request to create a user with username: {}", request.username());
        AuthenticationResponse authenticationResponse = userService.register(request);
        jwtService.addJwtCookieToResponse(servletResponse, authenticationResponse);
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request, HttpServletResponse servletResponse) {
        log.info("Received request to login with username: {}", request.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        AuthenticationResponse authenticationResponse = userService.login((User) authentication.getPrincipal());
        jwtService.addJwtCookieToResponse(servletResponse, authenticationResponse);
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        jwtService.clearJwtCookieFromResponse(response);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/settings")
    public ResponseEntity<SettingsDTO> updateUserSettings(@RequestBody SettingsDTO updateRequest, Authentication authentication) {
        String username = authentication.getName();
        SettingsDTO updatedSettings = userService.updateSettings(username, updateRequest);
        return ResponseEntity.ok(updatedSettings);
    }

    @GetMapping("/health")
    public ResponseEntity<Void> healthCheck() {
        // Used by frontend to determine if the server is running.
        return ResponseEntity.ok().build();
    }
}
