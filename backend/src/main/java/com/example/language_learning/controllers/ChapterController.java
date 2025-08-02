package com.example.language_learning.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    // You would inject a service here to handle the logic
    // @Autowired
    // private ChapterGenerationService chapterGenerationService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> generateChapter(@RequestBody GenerationRequest request) {
        // In a real app, you'd call a service that interacts with Ollama
        // and saves the result to a database.
        System.out.println("Received request to generate chapter for language: " + request.language() + " with topic: " + request.topic());

        // For now, we'll just return a mock response.
        // Replace this with a call to your ChapterGenerationService
        String mockJsonResponse = """
                {
                  "title": "Basic Korean Greetings",
                  "korean_title": "기초 한국어 인사",
                  "lessons": [
                    {
                      "type": "vocabulary",
                      "title": "Key Vocabulary",
                      "items": [
                        { "korean": "안녕하세요", "romanization": "annyeonghaseyo", "english": "Hello" },
                        { "korean": "감사합니다", "romanization": "gamsahamnida", "english": "Thank you" }
                      ]
                    }
                  ]
                }
                """;

        return Mono.just(mockJsonResponse);
        // return chapterGenerationService.generateAndSaveChapter(request.language(), request.topic());
    }
}

/**
 * A record to represent the incoming request body for chapter generation.
 *
 * @param language The target language (e.g., "Korean", "Japanese").
 * @param topic    The user-provided topic for the chapter (e.g., "Ordering food at a restaurant").
 */
record GenerationRequest(String language, String topic) {
}