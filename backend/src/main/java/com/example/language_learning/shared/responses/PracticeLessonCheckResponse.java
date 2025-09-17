package com.example.language_learning.shared.responses;

import lombok.Builder;

@Builder
public record PracticeLessonCheckResponse(
    boolean isCorrect,
    String correctedSentence,
    String feedback
) {}
