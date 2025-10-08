package com.example.language_learning.user;

import lombok.Builder;


import java.util.List;

@Builder
public record UserDTO (
    Long id,
    String username,
    String email,
    SettingsDTO settings
) {}
