package com.example.language_learning.user.requests;

import jakarta.validation.constraints.NotNull;


public record LoginRequest (

    @NotNull(message = "Username cannot be null")
    String username,

    @NotNull(message = "Password cannot be null")
    String password
) {}
