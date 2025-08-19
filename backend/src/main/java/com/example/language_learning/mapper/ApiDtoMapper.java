package com.example.language_learning.mapper;

import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ConjugationExampleDTO;
import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.entity.models.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Maps from the API-Response DTOs (sanitized) to the
 * application's internal DTOs.
 */
@Component
public class ApiDtoMapper {

    public ChapterMetadataDTO toChapterMetadataDTO(AIChapterMetadataResponse response, String topic) {
        return ChapterMetadataDTO.builder()
                .nativeTitle(response.nativeTitle())
                .title(response.title())
                .topic(topic)
                .build();
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIVocabularyLessonResponse response, String language) {
        return VocabularyLessonDTO.builder()
                .title(response.title())
                .vocabularies(response.vocabularies().stream()
                        .map(aiVocabulary -> {
                            return WordDTO.builder()
                                    .nativeWord(aiVocabulary.nativeWord())
                                    .language(language)
                                    .phoneticSpelling(aiVocabulary.phoneticSpelling())
                                    .englishTranslation(aiVocabulary.englishTranslation())
                                    .details(aiVocabulary.details())
                                    .build();
                        }).toList())
                .build();
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response) {
        return GrammarLessonDTO.builder()
                .title(response.title())
                .grammarConcept(response.grammarConcept())
                .nativeGrammarConcept(response.nativeGrammarConcept())
                .explanation(response.explanation())
                .exampleSentences(response.exampleSentences())
                .build();
    }

    public ConjugationLessonDTO toConjugationLessonDTO(AIConjugationLessonResponse response) {
        return ConjugationLessonDTO.builder()
                .title(response.title())
                .explanation(response.explanation())
                .conjugationRuleName(response.conjugationRuleName())
                .conjugatedWords(response.conjugatedWords().stream()
                        .map(aiConjugationExample -> {
                            return ConjugationExampleDTO.builder()
                                    .conjugatedForm(aiConjugationExample.conjugatedForm())
                                    .infinitive(aiConjugationExample.infinitive())
                                    .exampleSentence(aiConjugationExample.exampleSentence())
                                    .sentenceTranslation(aiConjugationExample.sentenceTranslation())
                                    .build();
                        }).toList())
                .build();
    }

    public PracticeLessonDTO toPracticeLessonDTO(AIPracticeLessonResponse response) {
        return PracticeLessonDTO.builder()
                .title(response.title())
                .instructions(response.instructions())
                .questions(response.questions().stream()
                        .map(aiQuestion -> {
                            return QuestionDTO.builder()
                                    .questionText(aiQuestion.questionText())
                                    .questionType(QuestionType.FREE_FORM.name())
                                    .answer(null)
                                    .options(Collections.emptyList())
                                    .build();
                        }).toList())
                .build();
    }

    public ReadingComprehensionLessonDTO toReadingComprehensionLessonDTO(AIReadingComprehensionLessonResponse response) {
        return ReadingComprehensionLessonDTO.builder()
                .title(response.title())
                .story(response.story())
                .questions(response.questions().stream()
                        .map(aiQuestion -> {
                            return QuestionDTO.builder()
                                    .questionText(aiQuestion.questionText())
                                    .questionType(QuestionType.MULTIPLE_CHOICE.name())
                                    .answer(aiQuestion.answer())
                                    .options(aiQuestion.options())
                                    .build();
                        }).toList())
                .build();
    }

}