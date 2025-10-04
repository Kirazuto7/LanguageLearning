package com.example.language_learning.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteOidcRegistrationRequest(
    @NotBlank String onboardingToken,
    @NotBlank @Size(min = 3, max = 20) String username,
    @NotBlank String language,
    @NotBlank String difficulty
) {}
