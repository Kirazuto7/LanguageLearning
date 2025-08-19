package com.example.language_learning.controllers;


import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.services.ChapterService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chapters")
@Slf4j
@RequiredArgsConstructor
public class ChapterController {

    private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);
    private final ChapterService chapterService;

    @PostMapping("/generate")
    public ChapterDTO generateChapter(@Valid @RequestBody ChapterGenerationRequest request) {
        logger.info("Received request to generate chapter for: {}", request);

        ChapterDTO chapterResponse = chapterService.generateNewChapter(request);
        logger.info("Successfully generated chapter: {}", chapterResponse.title());
        return chapterResponse;
    }

}