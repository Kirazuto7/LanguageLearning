package com.example.language_learning.dto.models;

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
    @JsonSubTypes.Type(value = GenericWordDetailsDTO.class, name = "GenericWord"),
    @JsonSubTypes.Type(value = JapaneseWordDetailsDTO.class, name = "JapaneseWord")
})
public interface WordDetailsDTO {
}
