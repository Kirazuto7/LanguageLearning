package com.example.language_learning.lessonbook.chapter.lesson.responses;

import lombok.Builder;

@Builder
public record PracticeLessonCheckResponse(
    boolean isCorrect,
    String correctedSentence,
    String feedback
) {}
