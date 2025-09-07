package com.example.language_learning.services;

import com.example.language_learning.dto.models.PageDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProgressService {
    private final Map<String, Sinks.Many<ProgressUpdateDTO>> taskSinks = new ConcurrentHashMap<>();

    public Flux<ProgressUpdateDTO> getPublisher(String taskId) {
        return taskSinks
                .computeIfAbsent(taskId, k -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux()
                .doFinally(signalType -> {
                    log.info("Stream for task {} finished with signal: {}. Removing sink.", taskId, signalType);
                    taskSinks.remove(taskId);
                });
    }

    public void sendUpdate(String taskId, int progress, String message) {
        log.info("Progress Update for Task {}: {}% - {}", taskId, progress, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forMessage(taskId, progress, message);
        send(taskId, update);
    }

    public void sendPageUpdate(String taskId, int progress, String message, Long chapterId, PageDTO page) {
        log.info("Progress Update for Task {}: {}% - {} (Page: {})", taskId, progress, message, page.lesson().type());
        boolean isComplete = (progress >= 100);
        ProgressUpdateDTO update = ProgressUpdateDTO.forPage(taskId, progress, message, chapterId, page, isComplete);
        send(taskId, update);
    }

    public void sendError(String taskId, Throwable error) {
        String errorMessage = "Chapter generation failed: " + error.getMessage();
        log.error("Task {} failed with error: {}", taskId, errorMessage, error); // Log the full stack trace
        ProgressUpdateDTO update = ProgressUpdateDTO.forError(taskId, errorMessage);
        send(taskId, update);
    }

    private void send(String taskId, ProgressUpdateDTO update) {
        Sinks.Many<ProgressUpdateDTO> sink = taskSinks.computeIfAbsent(taskId, k -> {
            log.info("Creating new sink for task {} on first update.", taskId);
            return Sinks.many().multicast().onBackpressureBuffer();
        });

        sink.tryEmitNext(update);

        if (update.isComplete() || update.error() != null) {
            sink.tryEmitComplete();
        }
    }
}
