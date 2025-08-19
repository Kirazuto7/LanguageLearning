package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.entity.lessons.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record VocabularyLessonDTO(
        Long id,
        String title,
        List<WordDTO> vocabularies
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.VOCABULARY;
    }
}
