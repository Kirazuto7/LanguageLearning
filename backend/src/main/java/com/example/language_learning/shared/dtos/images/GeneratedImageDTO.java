package com.example.language_learning.shared.dtos.images;

import java.util.List;

public record GeneratedImageDTO(
    List<String> urls,
    String originalPrompt
) {}
