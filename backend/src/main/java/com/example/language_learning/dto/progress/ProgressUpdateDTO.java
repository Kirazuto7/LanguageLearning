package com.example.language_learning.dto.progress;

import com.example.language_learning.dto.models.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressUpdateDTO(String taskId, int progress, String message, ProgressData data, String error, @JsonProperty("isComplete") boolean isComplete) {

    public static ProgressUpdateDTO forMessage(String taskId, int progress, String message) {
        return new ProgressUpdateDTO(taskId, progress, message, null, null, false);
    }

    public static ProgressUpdateDTO forData(String taskId, int progress, String message, PageDTO data) {
        return new ProgressUpdateDTO(taskId, progress, message, data, null, false);
    }

    public static ProgressUpdateDTO forCompletion(String taskId, String message) {
        return new ProgressUpdateDTO(taskId, 100, message, null, null,true);
    }

    public static ProgressUpdateDTO forError (String taskId, String errorMessage) {
        return new ProgressUpdateDTO(taskId, 0, null, null, errorMessage, true);
    }
}
