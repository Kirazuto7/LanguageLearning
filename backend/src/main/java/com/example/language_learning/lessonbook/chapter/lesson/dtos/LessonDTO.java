package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.shared.enums.LessonType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VocabularyLessonDTO.class, name = "VOCABULARY"),
        @JsonSubTypes.Type(value = PracticeLessonDTO.class, name = "PRACTICE"),
        @JsonSubTypes.Type(value = GrammarLessonDTO.class, name = "GRAMMAR"),
        @JsonSubTypes.Type(value = ConjugationLessonDTO.class, name = "CONJUGATION"),
        @JsonSubTypes.Type(value = ReadingComprehensionLessonDTO.class, name = "READING_COMPREHENSION")
})
public interface LessonDTO {
    Long id();
    LessonType type();
    String title();
}