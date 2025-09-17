package com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos;

import lombok.Builder;

@Builder
public record SentenceDTO (
    Long id,
    String text,
    String translation
) {}
