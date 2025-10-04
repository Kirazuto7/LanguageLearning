package com.example.language_learning.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest (

    @NotBlank @Size(min = 3, max = 20)
    @NotNull(message = "Username cannot be null")
    String username,

    @NotBlank @Email
    String email,

    @NotBlank @Size(min = 8, max = 20)
    @NotNull(message = "Password cannot be null")
    String password,

    @NotBlank(message = "Language cannot be blank")
    String language, // For initial settings

    @NotBlank(message = "Difficulty cannot be blank")
    String difficulty // For initial settings
) {}
