package com.example.language_learning.requests;

public record PracticeLessonCheckRequest(
    long questionId,
    String userSentence,
    String language,
    String difficulty
) {}
