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
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, mapper.toDto(user));
    }

    public User createNewUser(CreateUserRequest request) {
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

        java.util.Optional.ofNullable(updateRequest.getLanguage())
                .filter(lang -> !lang.isBlank())
                .ifPresent(settings::setLanguage);

        java.util.Optional.ofNullable(updateRequest.getDifficulty())
                .filter(diff -> !diff.isBlank())
                .ifPresent(settings::setDifficulty);

        userRepository.save(user);
        return mapper.toDto(settings);
    }
}
