package com.example.language_learning.requests;

import lombok.Builder;

@Builder
public record PracticeLessonCheckResponse(
    boolean isCorrect,
    String correctedSentence,
    String feedback
) {}
