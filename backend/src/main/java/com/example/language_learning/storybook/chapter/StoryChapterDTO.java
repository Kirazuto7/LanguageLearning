package com.example.language_learning.storybook.chapter;

import com.example.language_learning.storybook.chapter.page.StoryPageDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryChapterDTO(
    Long id,
    int chapterNumber,
    String title,
    String nativeTitle,
    List<StoryPageDTO> storyPages
) {}
