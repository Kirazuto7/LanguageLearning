package com.example.language_learning.dto.user;

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
