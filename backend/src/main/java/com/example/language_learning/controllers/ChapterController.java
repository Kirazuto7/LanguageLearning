package com.example.language_learning.controllers;

import java.util.List;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;
import com.example.language_learning.services.ChapterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);
    private final ChapterService chapterService;

    @PostMapping("/generate")
    public Mono<ChapterResponse> generateChapter(@RequestBody GenerationRequest request) {
        logger.info("Received request to generate chapter for language: {} with level: {} and topic: {}",
                request.getLanguage(), request.getLevel(), request.getTopic());

        // Return the Mono directly. Spring WebFlux will subscribe and handle the response.
        return chapterService.generateChapter(request)
            .doOnNext(chapterResponse -> logger.info("Successfully generated chapter: {}", chapterResponse.getTitle()));
    }

    @GetMapping("/load")
    public Mono<List<ChapterResponse>> loadChapters() {
        return null;
    }
}