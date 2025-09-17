package com.example.language_learning.shared.enums;

import java.util.regex.Pattern;

public enum SanitizationPattern {
    NON_ENGLISH_CHARS("[^a-zA-Z0-9\\s.,?!'\"\\-;:()\\[\\]]"),
    PARENTHETICAL_TEXT("\\s*\\([^)]*\\)");

    private final Pattern pattern;

    SanitizationPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String removeFrom(String input) {
        return this.pattern.matcher(input).replaceAll("");
    }
}
