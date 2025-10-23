package com.example.language_learning.shared.dtos.progress;

import lombok.Builder;

@Builder
public record CachedProgressUpdateDTO(
    ProgressUpdateDTO update,
    Long userId
) {}
