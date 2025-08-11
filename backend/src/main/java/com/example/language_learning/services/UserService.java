package com.example.language_learning.services;

import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.entity.Settings;
import com.example.language_learning.entity.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.UserRepository;
import com.example.language_learning.requests.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
}
