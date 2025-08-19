package com.example.language_learning.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest (

    @NotNull(message = "Username cannot be null")
    String username,

    @NotNull(message = "Password cannot be null")
    String password,

    @NotBlank(message = "Language cannot be blank")
    String language, // For initial settings

    @NotBlank(message = "Difficulty cannot be blank")
    String difficulty // For initial settings
) {}
