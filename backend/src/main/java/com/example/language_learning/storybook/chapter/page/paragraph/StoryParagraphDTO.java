package com.example.language_learning.storybook.chapter.page.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryParagraphDTO(
    Long id,
    int paragraphNumber,
    String content
) {}
