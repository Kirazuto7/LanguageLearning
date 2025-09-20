package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import lombok.Builder;

@Builder
public record LessonSentenceDTO(
    Long id,
    String text,
    String translation
) {}
