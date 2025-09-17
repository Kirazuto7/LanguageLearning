package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos.ConjugationExampleDTO;
import com.example.language_learning.shared.enums.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record ConjugationLessonDTO(
        Long id,
        String title,
        String conjugationRuleName,
        String explanation,
        List<ConjugationExampleDTO> conjugatedWords
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.CONJUGATION;
    }
}
