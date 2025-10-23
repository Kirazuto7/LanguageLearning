package com.example.language_learning.user;

import com.example.language_learning.user.dashboard.UserDataDTO;
import com.example.language_learning.user.requests.CompleteOidcRegistrationRequest;
import com.example.language_learning.user.requests.CreateUserRequest;
import com.example.language_learning.user.requests.LoginRequest;
import com.example.language_learning.security.AuthenticationResponse;
import com.example.language_learning.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        jwtService.addRefreshTokenCookieToResponse(servletResponse, authenticationResponse.refreshToken());
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/complete-oidc-registration")
    public ResponseEntity<UserDTO> completeOidcRegistration(@Valid @RequestBody CompleteOidcRegistrationRequest request, HttpServletResponse servletResponse) {
        log.info("Received request to complete OIDC registration for a new user.");
        AuthenticationResponse authenticationResponse = userService.completeOidcRegistration(request);
        jwtService.addJwtCookieToResponse(servletResponse, authenticationResponse);
        jwtService.addRefreshTokenCookieToResponse(servletResponse, authenticationResponse.refreshToken());
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
        jwtService.addRefreshTokenCookieToResponse(servletResponse, authenticationResponse.refreshToken());
        return ResponseEntity.ok(authenticationResponse.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Extract the refresh token from the cookie
        String refreshToken = jwtService.extractRefreshTokenFromRequest(request);

        // Invalidate the token in the db
        userService.logout(refreshToken);

        // Clear cookies from the client
        jwtService.clearJwtCookieFromResponse(response);
        jwtService.clearRefreshTokenCookieFromResponse(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.extractRefreshTokenFromRequest(request);
        try {
            AuthenticationResponse newAuthResponse = userService.refreshToken(refreshToken);
            jwtService.addJwtCookieToResponse(response, newAuthResponse);
            jwtService.addRefreshTokenCookieToResponse(response, newAuthResponse.refreshToken());
            return ResponseEntity.ok(newAuthResponse.user());
        }
        catch (Exception e) {
            jwtService.clearJwtCookieFromResponse(response);
            jwtService.clearRefreshTokenCookieFromResponse(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/settings")
    public ResponseEntity<SettingsDTO> updateUserSettings(@RequestBody SettingsDTO updateRequest, Authentication authentication) {
        String username = authentication.getName();
        SettingsDTO updatedSettings = userService.updateSettings(username, updateRequest);
        return ResponseEntity.ok(updatedSettings);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDataDTO> getUserDashboardData(Authentication authentication) {
        String username = authentication.getName();
        UserDataDTO userData = userService.getUserDashboardData(username);
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/health")
    public ResponseEntity<Void> healthCheck() {
        // Used by frontend to determine if the server is running.
        return ResponseEntity.ok().build();
    }
}
