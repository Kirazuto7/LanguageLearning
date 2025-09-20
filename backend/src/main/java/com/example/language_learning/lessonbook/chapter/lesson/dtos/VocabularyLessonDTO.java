package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.shared.word.dtos.WordDTO;
import com.example.language_learning.shared.enums.LessonType;
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
