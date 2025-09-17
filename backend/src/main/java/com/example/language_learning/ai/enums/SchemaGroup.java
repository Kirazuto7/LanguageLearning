package com.example.language_learning.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchemaGroup {
    JAPANESE("japanese"),
    KOREAN("korean"),
    CHINESE("chinese"),
    THAI("thai"),
    LATIN_EXTENDED("latin_extended");

    private final String pathValue;
}