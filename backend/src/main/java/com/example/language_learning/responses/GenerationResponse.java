package com.example.language_learning.responses;

import com.example.language_learning.dto.models.ChapterDTO;
import lombok.Builder;

@Builder
public record GenerationResponse(
        String taskId,
        ChapterDTO chapter
) {}
