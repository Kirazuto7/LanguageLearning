package com.example.language_learning.user.dashboard.library;

import java.time.LocalDateTime;

public record LessonBookLibraryItem(
    Long id,
    String title,
    String language,
    String difficulty,
    LocalDateTime createdAt,
    String type,
    int chapterCount,
    int pageCount
) implements LibraryItem {}
