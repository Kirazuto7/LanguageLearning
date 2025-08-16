package com.example.language_learning.dto.languages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Base class for all language-specific word DTOs.
 * This class uses Jackson annotations to handle polymorphic deserialization,
 * allowing the correct subclass (e.g., KoreanWordDTO) to be instantiated
 * based on the "type" property in the JSON input.
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EnglishWordDTO.class, names = {"english", "English", "ENGLISH"}),
    @JsonSubTypes.Type(value = JapaneseWordDTO.class, names = {"japanese", "Japanese", "JAPANESE"}),
    @JsonSubTypes.Type(value = KoreanWordDTO.class, names = {"korean", "Korean", "KOREAN"})
})
public abstract class WordDTO {
    private Long id;
    private String translation;

    /**
     * Returns the primary textual representation of the word in its native language,
     * suitable for use in subsequent AI prompts.
     */
    public abstract String getPrimaryRepresentation();
}