package com.example.language_learning.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Password cannot be null")
    private String password;

    @NotBlank(message = "Language cannot be blank")
    private String language; // For initial settings

    @NotBlank(message = "Difficulty cannot be blank")
    private String difficulty; // For initial settings
}
