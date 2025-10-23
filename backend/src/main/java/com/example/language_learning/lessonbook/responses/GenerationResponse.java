package com.example.language_learning.lessonbook.responses;

import com.example.language_learning.lessonbook.chapter.LessonChapterDTO;
import lombok.Builder;

@Builder
public record GenerationResponse(
        String taskId,
        LessonChapterDTO lessonChapter
) {}
