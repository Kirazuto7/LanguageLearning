package com.example.language_learning.storybook;

import com.example.language_learning.storybook.chapter.StoryChapterDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record StoryBookDTO(
    Long id,
    String title,
    String difficulty,
    String language,
    String genre,
    List<StoryChapterDTO> storyChapters
) {}
