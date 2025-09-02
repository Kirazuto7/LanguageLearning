package com.example.language_learning.dto.progress;

import com.example.language_learning.dto.models.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressUpdateDTO(String taskId, int progress, String message, PageDTO data, String error) {
    public ProgressUpdateDTO(String taskId, int progress, String message) {
        this(taskId, progress, message, null, null);
    }

    public ProgressUpdateDTO(String taskId, int progress, String message, PageDTO data) {
        this(taskId, progress, message, data, null);
    }
}
