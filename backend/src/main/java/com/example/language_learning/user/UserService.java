package com.example.language_learning.user;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.user.requests.CreateUserRequest;
import com.example.language_learning.security.AuthenticationResponse;
import com.example.language_learning.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthenticationResponse register(CreateUserRequest request) {
        User user = createNewUser(request);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(Instant.now().plusMillis(refreshTokenExpiration));
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapper.toDto(user))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(User user) {
        User managedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + user.getUsername()));
        String accessToken = jwtService.generateToken(managedUser);
        String refreshToken = jwtService.generateRefreshToken(managedUser);

        managedUser.setRefreshToken(refreshToken);
        managedUser.setRefreshTokenExpiry(Instant.now().plusMillis(refreshTokenExpiration));
        userRepository.save(managedUser);

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapper.toDto(managedUser))
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    user.setRefreshToken(null);
                    user.setRefreshTokenExpiry(null);
                    userRepository.save(user);
                    log.info("User {} logged out, refresh token invalidated.", user.getUsername());
                });
    }

    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token is missing from the request.");
        }
        UserDetails userDetails = loadUserByRefreshToken(refreshToken);
        User user = (User) userDetails;

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(Instant.now().plusMillis(refreshTokenExpiration));
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(mapper.toDto(user))
                .build();
    }

    @Transactional
    public User createNewUser(CreateUserRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        Settings settings = new Settings();
        settings.setLanguage(request.language());
        settings.setDifficulty(request.difficulty());
        settings.setTheme("default");
        settings.setMascot("jinny");
        settings.setAutoSpeakEnabled(true);
        user.setSettings(settings);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found during authentication process"));
    }

    @Transactional
    public UserDetails loadUserByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token is missing.");
        }

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token provided."));

        if (user.getRefreshTokenExpiry().isBefore(Instant.now())) {
            // Clean up the expired token from the database
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
            throw new BadCredentialsException("Refresh token has expired.");
        }

        return user;
    }

    @Transactional
    public SettingsDTO updateSettings(String username, SettingsDTO updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        Settings settings = user.getSettings();

        if (updateRequest.language() != null && !updateRequest.language().isBlank()) {
            settings.setLanguage(updateRequest.language());
        }
        if (updateRequest.difficulty() != null && !updateRequest.difficulty().isBlank()) {
            settings.setDifficulty(updateRequest.difficulty());
        }
        if (updateRequest.theme() != null && !updateRequest.theme().isBlank()) {
            settings.setTheme(updateRequest.theme());
        }

        if (updateRequest.mascot() != null && !updateRequest.mascot().isBlank()) {
            settings.setMascot(updateRequest.mascot());
        }

        settings.setAutoSpeakEnabled(updateRequest.autoSpeakEnabled());


        userRepository.save(user);
        return mapper.toDto(settings);
    }
}
