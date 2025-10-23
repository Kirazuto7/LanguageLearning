package com.example.language_learning.lessonbook.chapter.lesson.dtos;

import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionDTO;
import com.example.language_learning.shared.enums.LessonType;
import lombok.Builder;


import java.util.List;

@Builder
public record PracticeLessonDTO(
        Long id,
        String title,
        String instructions,
        List<LessonQuestionDTO> lessonQuestions
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.PRACTICE;
    }
}
