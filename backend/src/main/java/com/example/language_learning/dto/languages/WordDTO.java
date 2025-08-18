package com.example.language_learning.dto.languages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for all language-specific word DTOs.
 * This class uses Jackson annotations to handle polymorphic deserialization,
 * allowing the correct subclass (e.g., KoreanWordDTO) to be instantiated
 * based on the "type" property in the JSON input.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
    private Long id;
    private String englishTranslation;
    private String language;
    private String nativeWord;
    private String phoneticSpelling;
}