package com.example.language_learning.services;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProgressService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String taskId, SseEmitter emitter) {
        emitters.put(taskId, emitter);
        emitter.onCompletion(() -> emitters.remove(taskId));
        emitter.onTimeout(() -> emitters.remove(taskId));
        emitter.onError(e -> {
            log.error("SseEmitter error for task {}: {}", taskId, e.getMessage());
            emitters.remove(taskId);
        });
    }

    public void sendUpdate(String taskId, int progress, String message) {
        sendEvent(taskId, "progress-update", new ProgressUpdateDTO(progress, message));
    }

    public void sendComplete(String taskId, ChapterDTO chapter) {
        sendEvent(taskId, "complete", new ProgressUpdateDTO(100, "Complete", chapter));
        SseEmitter emitter = emitters.get(taskId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(taskId);
        }
    }

    public void sendError(String taskId, String errorMessage) {
        SseEmitter emitter = emitters.get(taskId);
        if(emitter != null) {
            emitter.completeWithError(new RuntimeException(errorMessage));
            emitters.remove(taskId);
        }
    }

    private void sendEvent(String taskId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(taskId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            }
            catch (Exception e) {
                log.warn("Failed to send event for task {}. Completing with error.", taskId, e);
                emitter.completeWithError(e);
            }
        }
    }
}
