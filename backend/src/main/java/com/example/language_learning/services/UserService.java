package com.example.language_learning.services;

import com.example.language_learning.dto.SettingsDTO;
import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.entity.Settings;
import com.example.language_learning.entity.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.UserRepository;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;

    public UserDTO createNewUser(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Settings settings = new Settings();
        settings.setLanguage(request.getLanguage());
        settings.setDifficulty(request.getDifficulty());
        user.setSettings(settings);

        User savedUser = userRepository.save(user);
        return mapper.toDto(savedUser);
    }

    public UserDTO login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!passwordEncoder.matches(user.getPassword(), request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return mapper.toDto(user);
    }

    public SettingsDTO updateSettings(Long userId, SettingsDTO updateRequest) {
        User user = userRepository.findById(userId).orElseThrow( () ->
                new IllegalArgumentException("User with id: " + userId + " not found.")
        );

        if(updateRequest.getLanguage() != null && !updateRequest.getLanguage().isBlank()) {
            user.getSettings().setLanguage(updateRequest.getLanguage());
        }
        if(updateRequest.getDifficulty() != null && !updateRequest.getDifficulty().isBlank()) {
            user.getSettings().setDifficulty(updateRequest.getDifficulty());
        }
        User savedUser = userRepository.save(user);
        return mapper.toDto(savedUser.getSettings());
    }
}
