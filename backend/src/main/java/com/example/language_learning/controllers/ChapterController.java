package com.example.language_learning.controllers;

import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final ProgressService progressService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> requestChapterGeneration(@RequestBody ChapterGenerationRequest request) {
        String taskId = UUID.randomUUID().toString();
        chapterService.generateNewChapterStream(request, taskId);
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    @GetMapping("/progress/{taskId}")
    public SseEmitter streamProgress(@PathVariable String taskId) {
        SseEmitter emitter = new SseEmitter(1_800_000L);
        progressService.addEmitter(taskId, emitter);
        return emitter;
    }
}
