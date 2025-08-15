package com.example.language_learning.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterGenerationRequest {

    @NotBlank(message = "Language cannot be blank")
    private String language;

    @NotBlank(message = "Difficulty cannot be blank")
    private String difficulty;

    @NotBlank(message = "Topic cannot be blank")
    private String topic;

    @NotNull(message = "User ID cannot be null")
    private Long userId;
}