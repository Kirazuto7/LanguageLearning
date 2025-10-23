package com.example.language_learning.user.dashboard.library;

import java.time.LocalDateTime;

public record StoryBookLibraryItem(
    Long id,
    String title,
    String language,
    String difficulty,
    LocalDateTime createdAt,
    String type,
    int storyCount,
    int pageCount
) implements LibraryItem {}
