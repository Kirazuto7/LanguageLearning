package com.example.language_learning.dto.languages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KoreanWordDTO.class, name = "korean"),
        @JsonSubTypes.Type(value = JapaneseWordDTO.class, name = "japanese")
})
@Data
public abstract class WordDTO {
    private Long id;
    private String translation;
}
