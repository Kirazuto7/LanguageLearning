package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.PageDTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VocabularyLessonDTO.class, name = "vocabulary"),
        @JsonSubTypes.Type(value = SentenceLessonDTO.class, name = "sentence"),
        @JsonSubTypes.Type(value = GrammarLessonDTO.class, name = "grammar"),
        @JsonSubTypes.Type(value = ReadingComprehensionLessonDTO.class, name = "reading_comprehension")
})
public abstract class LessonDTO {
    private Long id;
    private String type;
    private String title;
    private PageDTO page;
}