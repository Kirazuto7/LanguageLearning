package com.example.language_learning.lessonbook;

import java.time.LocalDateTime;
import java.util.List;

import com.example.language_learning.lessonbook.chapter.LessonChapterDTO;
import lombok.Builder;

@Builder
public record LessonBookDTO (
    Long id,
    String title,
    String difficulty,
    String language,
    LocalDateTime createdAt,
    List<LessonChapterDTO> lessonChapters
) {}
