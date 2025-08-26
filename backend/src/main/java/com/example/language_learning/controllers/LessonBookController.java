package com.example.language_learning.controllers;

import com.example.language_learning.requests.PracticeLessonCheckRequest;
import com.example.language_learning.requests.PracticeLessonCheckResponse;
import com.example.language_learning.services.PracticeLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lessonbook")
@RequiredArgsConstructor
public class LessonBookController {

    private final PracticeLessonService practiceLessonService;

    @PostMapping("/practice/proofread")
    public ResponseEntity<PracticeLessonCheckResponse> checkPracticeLessonSentence(@RequestBody PracticeLessonCheckRequest request) {
        return ResponseEntity.ok(practiceLessonService.checkSentence(request));
    }
}
