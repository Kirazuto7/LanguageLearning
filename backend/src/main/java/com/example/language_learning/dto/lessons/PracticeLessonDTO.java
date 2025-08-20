package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.enums.LessonType;
import lombok.Builder;


import java.util.List;

@Builder
public record PracticeLessonDTO(
        Long id,
        String title,
        String instructions,
        List<QuestionDTO> questions
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.PRACTICE;
    }
}
