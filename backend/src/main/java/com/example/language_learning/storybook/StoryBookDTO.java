package com.example.language_learning.storybook;

import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record StoryBookDTO(
    Long id,
    String title,
    String difficulty,
    String language,
    LocalDateTime createdAt,
    List<ShortStoryDTO> shortStories
) {}
