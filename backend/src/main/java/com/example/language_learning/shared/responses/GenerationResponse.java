package com.example.language_learning.shared.responses;

import com.example.language_learning.lessonbook.chapter.dtos.ChapterDTO;
import lombok.Builder;

@Builder
public record GenerationResponse(
        String taskId,
        ChapterDTO chapter
) {}
