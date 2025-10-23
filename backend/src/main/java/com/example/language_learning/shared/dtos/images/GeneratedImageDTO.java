package com.example.language_learning.shared.dtos.images;

import java.util.List;
import java.util.Map;

public record GeneratedImageDTO(
    Map<String, String> imageUrlsByPrompt,
    String originalPrompt
) {}
