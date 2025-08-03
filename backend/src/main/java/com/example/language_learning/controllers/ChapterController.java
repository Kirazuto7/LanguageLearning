package com.example.language_learning.controllers;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;
import com.example.language_learning.services.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {
    
    private final ChapterService chapterService;

    @PostMapping("/generate")
    public Mono<ChapterResponse> generateChapter(@RequestBody GenerationRequest request) {
        System.out.println("Received request to generate chapter for language: " + request.getLanguage() + " with level: " + request.getLevel() + " and topic: " + request.getTopic());

        // Return the Mono directly. Spring WebFlux will subscribe and handle the response.
        return chapterService.generateChapter(request)
                .doOnNext(chapterResponse -> System.out.println("Generated chapter: " + chapterResponse.getTitle()));
    }
}