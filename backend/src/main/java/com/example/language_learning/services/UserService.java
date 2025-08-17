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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(mapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public SettingsDTO updateSettings(String username, SettingsDTO updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

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
