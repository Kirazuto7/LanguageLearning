package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShortStoryDTO(
    Long id,
    String title,
    String nativeTitle,
    String genre,
    String topic,
    List<StoryPageDTO> storyPages
) {}
