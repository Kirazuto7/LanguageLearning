package com.example.language_learning.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstructionGroup {
    JAPANESE("japanese"),
    KOREAN("korean"),
    CHINESE("chinese"),
    THAI("thai"),
    ITALIAN("italian"),
    SPANISH("spanish"),
    FRENCH("french"),
    GERMAN("german");

    private final String pathValue;
}