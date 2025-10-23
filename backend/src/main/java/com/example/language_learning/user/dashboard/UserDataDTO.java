package com.example.language_learning.user.dashboard;

import com.example.language_learning.user.dashboard.library.LessonBookLibraryItem;
import com.example.language_learning.user.dashboard.library.StoryBookLibraryItem;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDataDTO (
    List<LessonBookLibraryItem> lessonBooks,
    List<StoryBookLibraryItem> storyBooks
){}
