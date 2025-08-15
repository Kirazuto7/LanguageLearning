package com.example.language_learning.mapper;

import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import org.springframework.stereotype.Component;

/**
 * Maps from API-specific DTOs (from the Anti-Corruption Layer) to the
 * application's internal DTOs.
 */
@Component
public class ApiDtoMapper {

    public ChapterMetadataDTO toChapterMetadataDTO(AIChapterMetadataResponse response, String topic) {
        ChapterMetadataDTO dto = new ChapterMetadataDTO();
        dto.setTitle(response.title());
        dto.setNativeTitle(response.nativeTitle());
        dto.setTopic(topic);
        return dto;
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIVocabularyLessonResponse response) {
        VocabularyLessonDTO dto = new VocabularyLessonDTO();
        dto.setTitle(response.title());
        dto.setVocabularies(response.vocabularies());
        return dto;
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response) {
        GrammarLessonDTO dto = new GrammarLessonDTO();
        dto.setTitle(response.title());
        dto.setGrammarConcept(response.grammarConcept());
        dto.setExplanation(response.explanation());
        dto.setExampleSentences(response.exampleSentences());
        return dto;
    }

    public PracticeLessonDTO toPracticeLessonDTO(AIPracticeLessonResponse response) {
        PracticeLessonDTO dto = new PracticeLessonDTO();
        dto.setTitle(response.title());
        dto.setInstructions(response.instructions());
        dto.setQuestions(response.questions());
        return dto;
    }

    public ReadingComprehensionLessonDTO toReadingComprehensionLessonDTO(AIReadingComprehensionLessonResponse response) {
        ReadingComprehensionLessonDTO dto = new ReadingComprehensionLessonDTO();
        dto.setTitle(response.title());
        dto.setStory(response.story());
        dto.setQuestions(response.questions());
        return dto;
    }

}