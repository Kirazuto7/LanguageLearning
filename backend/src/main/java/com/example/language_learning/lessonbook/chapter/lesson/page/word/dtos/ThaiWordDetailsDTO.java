package com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos;

import lombok.Builder;

@Builder
public record ThaiWordDetailsDTO(
        String thaiScript,
        String romanization,
        String tonePattern
) implements WordDetailsDTO {}