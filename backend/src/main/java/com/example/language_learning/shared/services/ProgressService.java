package com.example.language_learning.shared.services;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.shared.dtos.progress.CachedProgressUpdateDTO;
import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProgressService {
    private final Map<String, Sinks.Many<ProgressUpdateDTO>> taskSinks = new ConcurrentHashMap<>();
    private final Map<String, CachedProgressUpdateDTO> latestTaskUpdates = new ConcurrentHashMap<>();

    public Flux<ProgressUpdateDTO> getProgressUpdate(String taskId, User user) {
        CachedProgressUpdateDTO cachedUpdate = getLatestUpdate(taskId);

        if (cachedUpdate != null && !cachedUpdate.userId().equals(user.getId())) {
            return Flux.error(new AccessDeniedException("User does not have permission to access this task."));
        }

        if (cachedUpdate != null && (cachedUpdate.update().isComplete() || cachedUpdate.update().isError())) {
            log.info("Client subscribed to a FINISHED task {}. Emitting stored final state.", taskId);
            return Flux.just(cachedUpdate.update());
        }

        return taskSinks
                .computeIfAbsent(taskId, k -> {
                    log.info("Creating new sink for task {}.", taskId);
                    return Sinks.many().multicast().onBackpressureBuffer();
                })
                .asFlux();
    }

    public void sendUpdate(String taskId, int progress, String message, User user) {
        log.info("Progress Update for Task {}: {}% - {}", taskId, progress, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forMessage(taskId, progress, message);
        send(taskId, update, user);
    }

    public void sendPageUpdate(String taskId, int progress, String message, LessonPageDTO page, User user) {
        log.info("Progress Update for Task {}: {}% - {} (LessonPage Data: {})", taskId, progress, message, page.lesson().type());
        ProgressUpdateDTO update = ProgressUpdateDTO.forData(taskId, progress, message, page);
        send(taskId, update, user);
    }

    public void sendPageUpdate(String taskId, int progress, String message, StoryPageDTO storyPage, User user) {
        log.info("Progress Update for Task {}: {}% - {} (StoryPage Data: {})", taskId, progress, message, storyPage);
        ProgressUpdateDTO update = ProgressUpdateDTO.forData(taskId, progress, message, storyPage);
        send(taskId, update, user);
    }

    public void sendCompletion(String taskId, String message, User user) {
        log.info("Sending completion for Task {}: {}", taskId, message);
        ProgressUpdateDTO update = ProgressUpdateDTO.forCompletion(taskId, message);
        send(taskId, update, user);
        completeSink(taskId);
    }

    public void sendError(String taskId, Throwable error, User user) {
        String errorMessage = "Task execution failed: " + error.getMessage();
        log.error("Sending error for Task {}: {}", taskId, errorMessage, error);
        ProgressUpdateDTO update = ProgressUpdateDTO.forError(taskId, errorMessage);
        send(taskId, update, user);
        completeSink(taskId);
    }

    private void completeSink(String taskId) {
        Sinks.Many<ProgressUpdateDTO> sink = taskSinks.remove(taskId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    private void send(String taskId, ProgressUpdateDTO update, User user) {
        latestTaskUpdates.put(taskId, CachedProgressUpdateDTO.builder().userId(user.getId()).update(update).build());

        if (update.data() != null) {
            log.info("SENDING update for task {} with data {}", taskId, update.data());
        }
        if (update.isError()) {
            log.info("SENDING error update for task {}: {}", taskId, update.message());
        }
        else {
            log.debug("SENDING message-only update for task {}: {}", taskId, update.message());
        }

        var sink = taskSinks.get(taskId);
        if (sink != null) {
            var result = sink.tryEmitNext(update);
            if (result.isFailure()) {
                log.warn("Failed to emit progress update for task {}. Result: {}", taskId, result);
            }
            else {
                log.debug("No active sink for task {}. Update will be cached in latestTaskUpdates.", taskId);
            }
        }

        /*Sinks.Many<ProgressUpdateDTO> sink = taskSinks.computeIfAbsent(taskId, k -> {
            log.info("Creating new sink for task {} on first update.", taskId);
            return Sinks.many().multicast().onBackpressureBuffer();
        });

        Sinks.EmitResult result = sink.tryEmitNext(update);
        if (result.isFailure()) {
            log.warn("Failed to emit progress update for task {}. Result: {}", taskId, result);
        }*/
    }

    public CachedProgressUpdateDTO getLatestUpdate(String taskId) {
        return latestTaskUpdates.get(taskId);
    }

    public Sinks.Many<ProgressUpdateDTO> getSink(String taskId) {
        return taskSinks.get(taskId);
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void cleanupOldTasks() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(30));
        latestTaskUpdates.entrySet().removeIf(entry -> {
            ProgressUpdateDTO update = entry.getValue().update();
            return (update.isComplete() || update.isError()) && update.timestamp().isBefore(cutoff);
        });
    }
}
