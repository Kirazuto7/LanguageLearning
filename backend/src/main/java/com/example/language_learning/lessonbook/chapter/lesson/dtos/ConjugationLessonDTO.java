package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExampleDTO;
import com.example.language_learning.shared.enums.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record ConjugationLessonDTO(
        Long id,
        String title,
        String conjugationRuleName,
        String explanation,
        List<LessonConjugationExampleDTO> conjugatedWords
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.CONJUGATION;
    }
}
