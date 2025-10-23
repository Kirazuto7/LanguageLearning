package com.example.language_learning.shared.word.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A DTO interface for language-specific word details.
 * The annotations instruct Jackson to include a 'type' field in the JSON output,
 * which allows the frontend to easily distinguish between different kinds of word details.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = JapaneseWordDetailsDTO.class, name = "JapaneseWord"),
    @JsonSubTypes.Type(value = KoreanWordDetailsDTO.class, name = "KoreanWord"),
    @JsonSubTypes.Type(value = ChineseWordDetailsDTO.class, name = "ChineseWord"),
    @JsonSubTypes.Type(value = ThaiWordDetailsDTO.class, name = "ThaiWord"),
    @JsonSubTypes.Type(value = ItalianWordDetailsDTO.class, name = "ItalianWord"),
    @JsonSubTypes.Type(value = SpanishWordDetailsDTO.class, name = "SpanishWord"),
    @JsonSubTypes.Type(value = FrenchWordDetailsDTO.class, name = "FrenchWord"),
    @JsonSubTypes.Type(value = GermanWordDetailsDTO.class, name = "GermanWord")
})
public interface WordDetailsDTO {
}
