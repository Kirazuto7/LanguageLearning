package com.example.language_learning.controllers;


import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.services.ChapterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);
    private final ChapterService chapterService;

    @PostMapping("/generate")
    public Mono<ChapterDTO> generateChapter(@RequestBody ChapterGenerationRequest request) {
        logger.info("Received request to generate chapter for language: {} with level: {} and topic: {}",
                request.getLanguage(), request.getDifficulty(), request.getTopic());

        return chapterService.generateNewChapter(request)
            .doOnNext(chapterResponse -> logger.info("Successfully generated chapter: {}", chapterResponse.getTitle()));
    }

}