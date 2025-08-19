package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.entity.lessons.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record ReadingComprehensionLessonDTO(
        Long id,
        String title,
        String story,
        List<QuestionDTO> questions
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.READING_COMPREHENSION;
    }
}
