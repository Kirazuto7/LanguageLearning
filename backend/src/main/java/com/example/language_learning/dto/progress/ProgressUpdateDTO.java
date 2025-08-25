package com.example.language_learning.dto.progress;

import com.example.language_learning.dto.models.ChapterDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressUpdateDTO(int progress, String message, ChapterDTO data) {
    public ProgressUpdateDTO(int progress, String message) {
        this(progress, message, null);
    }
}
