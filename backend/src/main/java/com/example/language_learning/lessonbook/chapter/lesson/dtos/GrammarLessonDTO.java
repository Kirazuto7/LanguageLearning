package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentenceDTO;
import com.example.language_learning.shared.enums.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record GrammarLessonDTO(
        Long id,
        String title,
        String grammarConcept,
        String explanation,
        List<LessonSentenceDTO> exampleLessonSentences
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.GRAMMAR;
    }
}
