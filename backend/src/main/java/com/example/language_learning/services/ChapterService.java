package com.example.language_learning.services;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;
import com.example.language_learning.dto.Lesson;
import com.example.language_learning.dto.VocabularyItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final AIService aiService;

    public Mono<ChapterResponse> generateChapter(GenerationRequest request) {
        // This method currently returns a mock response for testing purposes.
        // To use the real AI service, comment out the line below and uncomment the following one.
        return Mono.just(createMockChapterResponse(request));
        // return aiService.generateChapter(request);
    }

    private ChapterResponse createMockChapterResponse(GenerationRequest request) {
        System.out.println("Generating MOCK chapter for topic: " + request.getTopic());

        var vocabItem1 = new VocabularyItem("안녕하세요", "annyeonghaseyo", "Hello");
        var vocabItem2 = new VocabularyItem("감사합니다", "gamsahamnida", "Thank you");
        var vocabItem3 = new VocabularyItem("네", "ne", "Yes");
        var vocabItem4 = new VocabularyItem("아니요", "aniyo", "No");

        var lesson = new Lesson("vocabulary", "Key Vocabulary for '" + request.getTopic() + "'", List.of(vocabItem1, vocabItem2, vocabItem3, vocabItem4));

        return new ChapterResponse(
                "Mock Chapter: " + request.getTopic(),
                "Mock Native Title",
                List.of(lesson)
        );
    }
}
