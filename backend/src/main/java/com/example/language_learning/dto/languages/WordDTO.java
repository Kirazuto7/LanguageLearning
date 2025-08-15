package com.example.language_learning.dto.languages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KoreanWordDTO.class, name = "korean"),
        @JsonSubTypes.Type(value = JapaneseWordDTO.class, name = "japanese"),
        @JsonSubTypes.Type(value = EnglishWordDTO.class, name = "english")
})
public abstract class WordDTO {
    private Long id;
    private String translation;
}