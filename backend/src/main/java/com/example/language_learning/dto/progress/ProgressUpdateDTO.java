package com.example.language_learning.dto.progress;

import com.example.language_learning.dto.models.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressUpdateDTO(String taskId, int progress, String message, PageDTO data) {
    public ProgressUpdateDTO(String taskId, int progress, String message) {
        this(taskId, progress, message, null);
    }
}
