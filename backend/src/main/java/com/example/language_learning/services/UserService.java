package com.example.language_learning.services;

import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.entity.user.Settings;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.UserRepository;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import com.example.language_learning.security.AuthenticationResponse;
import com.example.language_learning.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;
    private final JwtService jwtService;

    public AuthenticationResponse register(CreateUserRequest request) {
        User user = createNewUser(request);
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, mapper.toDto(user));
    }

    public AuthenticationResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, mapper.toDto(user));
    }

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
        user.setSettings(settings);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));
    }

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

        userRepository.save(user);
        return mapper.toDto(settings);
    }
}
