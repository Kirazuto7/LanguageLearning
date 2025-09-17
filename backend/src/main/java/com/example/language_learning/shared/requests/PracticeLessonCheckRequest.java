package com.example.language_learning.shared.requests;

public record PracticeLessonCheckRequest(
    long questionId,
    String userSentence,
    String language,
    String difficulty
) {}
