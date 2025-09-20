package com.example.language_learning.lessonbook.chapter.lesson.requests;

public record PracticeLessonCheckRequest(
    long questionId,
    String userSentence,
    String language,
    String difficulty
) {}
