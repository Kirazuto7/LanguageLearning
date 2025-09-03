package com.example.language_learning.services;

import com.example.language_learning.dto.models.PageDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class ProgressService {
    // Broadcast to all graphql subscribers
    private final Sinks.Many<ProgressUpdateDTO> sink = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<ProgressUpdateDTO> getPublisher() {
        return sink.asFlux();
    }

    public void sendUpdate(String taskId, int progress, String message) {
        log.info("Progress Update for Task {}: {}% - {}", taskId, progress, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forMessage(taskId, progress, message);
        sink.tryEmitNext(update);
    }

    public void sendPageUpdate(String taskId, int progress, String message, Long chapterId, PageDTO page) {
        log.info("Progress Update for Task {}: {}% - {} (Page: {})", taskId, progress, message, page.lesson().type());
        ProgressUpdateDTO update = ProgressUpdateDTO.forPage(taskId, progress, message, chapterId, page);
        sink.tryEmitNext(update);
    }

    public void sendError(String taskId, Throwable error) {
        String errorMessage = "Chapter generation failed: " + error.getMessage();
        log.error("Task {} failed with error: {}", taskId, errorMessage, error); // Log the full stack trace
        ProgressUpdateDTO update = ProgressUpdateDTO.forError(taskId, errorMessage);
        sink.tryEmitNext(update);
    }
}
