package com.example.language_learning.shared.services;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class  ProgressService {
    private final Map<String, Sinks.Many<ProgressUpdateDTO>> taskSinks = new ConcurrentHashMap<>();

    public Flux<ProgressUpdateDTO> getPublisher(String taskId) {
        // The sink is now only removed when the task completes or errors, not when a client disconnects.
        return taskSinks
                .computeIfAbsent(taskId, k -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux()
                .doOnCancel(() -> log.info("Client disconnected from task {}. Sink will remain for other subscribers.", taskId));
    }

    public void sendUpdate(String taskId, int progress, String message) {
        log.info("Progress Update for Task {}: {}% - {}", taskId, progress, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forMessage(taskId, progress, message);
        send(taskId, update);
    }

    public void sendPageUpdate(String taskId, int progress, String message, LessonPageDTO page) {
        log.info("Progress Update for Task {}: {}% - {} (Data: {})", taskId, progress, message, page.lesson().type());
        ProgressUpdateDTO update = ProgressUpdateDTO.forData(taskId, progress, message, page);
        send(taskId, update);
    }

    public void sendPageUpdate(String taskId, int progress, String message, StoryPageDTO storyPage) {
        log.info("Progress Update for Task {}: {}% - {} (Data: {})", taskId, progress, message, storyPage);
        ProgressUpdateDTO update = ProgressUpdateDTO.forData(taskId, progress, message, storyPage);
        send(taskId, update);
    }

    public void sendCompletion(String taskId, String message) {
        log.info("Completion for Task {}: {}", taskId, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forCompletion(taskId, message);
        send(taskId, update);

        // When the task is complete, signal all listeners and remove the sink.
        Sinks.Many<ProgressUpdateDTO> sink = taskSinks.remove(taskId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    public void sendError(String taskId, Throwable error) {
        String errorMessage = "Task execution failed: " + error.getMessage();
        log.error("Task {} failed with error: {}", taskId, errorMessage, error); // Log the full stack trace
        ProgressUpdateDTO update = ProgressUpdateDTO.forError(taskId, errorMessage);
        send(taskId, update);

        // When the task errors, signal all listeners and remove the sink.
        Sinks.Many<ProgressUpdateDTO> sink = taskSinks.remove(taskId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    private void send(String taskId, ProgressUpdateDTO update) {
        // Enhanced logging to debug the exact content of every update being sent.
        if (update.data() != null) {
            log.debug("SENDING update for task {} with data {}", taskId, update.data());
        } else if (update.error() != null) {
            log.debug("SENDING error update for task {}: {}", taskId, update.error());
        } else {
            log.debug("SENDING message-only update for task {}: {}", taskId, update.message());
        }

        Sinks.Many<ProgressUpdateDTO> sink = taskSinks.computeIfAbsent(taskId, k -> {
            log.info("Creating new sink for task {} on first update.", taskId);
            return Sinks.many().multicast().onBackpressureBuffer();
        });

        sink.tryEmitNext(update);
    }
}
