package com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos;

import lombok.Builder;

@Builder
public record ChineseWordDetailsDTO(
        String simplified,
        String traditional,
        String pinyin,
        String toneNumber
) implements WordDetailsDTO {}