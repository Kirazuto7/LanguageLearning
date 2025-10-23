package com.example.language_learning.shared.dtos.progress;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressUpdateDTO(String taskId, int progress, String message, ProgressData data, @JsonProperty("isError") boolean isError, @JsonProperty("isComplete") boolean isComplete, @JsonIgnore Instant timestamp) {

    public static ProgressUpdateDTO forMessage(String taskId, int progress, String message) {
        return new ProgressUpdateDTO(taskId, progress, message, null, false, false, Instant.now());
    }

    public static ProgressUpdateDTO forData(String taskId, int progress, String message, LessonPageDTO data) {
        return new ProgressUpdateDTO(taskId, progress, message, data, false, false, Instant.now());
    }

    public static ProgressUpdateDTO forData(String taskId, int progress, String message, StoryPageDTO data) {
        return new ProgressUpdateDTO(taskId, progress, message, data, false, false, Instant.now());
    }

    public static ProgressUpdateDTO forCompletion(String taskId, String message) {
        return new ProgressUpdateDTO(taskId, 100, message, null, false,true, Instant.now());
    }

    public static ProgressUpdateDTO forError (String taskId, String errorMessage) {
        return new ProgressUpdateDTO(taskId, 0, errorMessage, null, true, false, Instant.now());
    }
}
