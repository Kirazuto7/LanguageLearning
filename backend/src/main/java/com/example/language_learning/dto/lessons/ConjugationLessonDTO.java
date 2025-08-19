package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.ConjugationExampleDTO;
import com.example.language_learning.entity.lessons.LessonType;
import lombok.Builder;

import java.util.List;

@Builder
public record ConjugationLessonDTO(
        Long id,
        String title,
        String conjugationRuleName,
        String explanation,
        List<ConjugationExampleDTO> conjugationTable
) implements LessonDTO {
    @Override
    public LessonType type() {
        return LessonType.CONJUGATION;
    }
}
