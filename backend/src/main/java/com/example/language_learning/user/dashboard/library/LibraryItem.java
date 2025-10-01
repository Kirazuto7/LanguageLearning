package com.example.language_learning.user.dashboard.library;

import java.time.LocalDateTime;

public sealed interface LibraryItem permits LessonBookLibraryItem, StoryBookLibraryItem {
    Long id();
    String title();
    String language();
    String difficulty();
    LocalDateTime createdAt();
    String type();
}
