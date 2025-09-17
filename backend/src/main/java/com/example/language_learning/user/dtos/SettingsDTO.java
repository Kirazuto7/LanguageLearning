package com.example.language_learning.user.dtos;

import lombok.Builder;


@Builder
public record SettingsDTO (
    Long id,
    String language,
    String difficulty,
    String theme,
    String mascot,
    boolean autoSpeakEnabled
) {}
