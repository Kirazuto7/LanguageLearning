package com.example.language_learning.services;

import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.entity.Settings;
import com.example.language_learning.entity.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.UserRepository;
import com.example.language_learning.requests.CreateUserRequest;
import com.example.language_learning.requests.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public Mono<UserDTO> createNewUser(CreateUserRequest request) {
        return Mono.fromCallable(() -> {
            // This entire block of blocking code is deferred until subscription.
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                // Throwing an exception here will result in an `onError` signal in the Mono.
                throw new IllegalArgumentException("Username already exists");
            }

            if(request.getUsername().isEmpty() || request.getPassword().isEmpty() || request.getLanguage().isEmpty() || request.getLanguage().isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            Settings settings = new Settings();
            settings.setLanguage(request.getLanguage());
            settings.setDifficulty(request.getDifficulty());
            user.setSettings(settings);
            User savedUser = userRepository.save(user);
            // The final expression is the success value of the Mono.
            return mapper.toDto(savedUser);
        });
    }

    public Mono<UserDTO> login(LoginRequest request) {
        return Mono.fromCallable(() -> {
            if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
                throw new IllegalArgumentException("User not found.");
            }
            User user = userRepository.findByUsername(request.getUsername()).get();

            if(!Objects.equals(user.getPassword(), request.getPassword())) {
                throw new IllegalArgumentException("Incorrect password.");
            } else {
                return mapper.toDto(user);
            }
        });
    }
}
