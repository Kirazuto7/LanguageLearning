package com.example.language_learning.storybook.chapter.page;

import com.example.language_learning.shared.word.dtos.WordDTO;
import com.example.language_learning.storybook.chapter.page.paragraph.StoryParagraphDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryPageDTO(
    Long id,
    int pageNumber,
    List<StoryParagraphDTO> paragraphs,
    List<WordDTO> vocabulary
) {}
