package com.example.language_learning.storybook.shortstory.page.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.Set;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryParagraphDTO(
    Long id,
    int paragraphNumber,
    String content,
    Set<String> wordsToHighlight
) {}
