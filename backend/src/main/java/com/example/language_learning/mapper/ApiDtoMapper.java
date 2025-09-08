package com.example.language_learning.mapper;

import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.enums.QuestionType;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.FuriganaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Maps from the API-Response DTOs (sanitized) to the
 * application's internal DTOs.
 */
@Component
@RequiredArgsConstructor
public class ApiDtoMapper {

    private final FuriganaService furiganaService;

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
                        .filter(aiVocabulary -> aiVocabulary.nativeWord() != null && !aiVocabulary.nativeWord().isBlank())
                        .map(aiVocabulary -> {
                            return WordDTO.builder()
                                    .nativeWord(aiVocabulary.nativeWord())
                                    .language(language)
                                    .phoneticSpelling(aiVocabulary.phoneticSpelling())
                                    .englishTranslation(aiVocabulary.englishTranslation())
                                    .details(aiVocabulary.details())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIJapaneseVocabularyLessonResponse response, String language) {
        return VocabularyLessonDTO.builder()
                .title(response.title())
                .vocabularies(response.vocabularies().stream()
                        .filter(aiVocabulary -> aiVocabulary.englishTranslation() != null && !aiVocabulary.englishTranslation().isBlank())
                        .map(furiganaService::verifyAndMapJapaneseWord)
                        .collect(Collectors.toList()))
                .build();
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response, String language) {
        return GrammarLessonDTO.builder()
                .title(response.title())
                .grammarConcept(response.grammarConcept())
                .nativeGrammarConcept(response.nativeGrammarConcept())
                .explanation(response.explanation())
                .exampleSentences(response.exampleSentences().stream()
                        .filter(aiSentence -> aiSentence.text() != null && !aiSentence.text().isBlank())
                        .map(aiSentence -> {
                            String text = "japanese".equalsIgnoreCase(language)
                                    ? furiganaService.addFurigana(aiSentence.text())
                                    : aiSentence.text();
                            return SentenceDTO.builder()
                                    .text(text)
                                    .translation(aiSentence.translation())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public ConjugationLessonDTO toConjugationLessonDTO(AIConjugationLessonResponse response, String language) {
        return ConjugationLessonDTO.builder()
                .title(response.title())
                .explanation(response.explanation())
                .conjugationRuleName(response.conjugationRuleName())
                .conjugatedWords(response.conjugatedWords().stream()
                        .filter(aiConjugationWord -> aiConjugationWord.conjugatedForm() != null && !aiConjugationWord.conjugatedForm().isBlank())
                        .map(aiConjugationExample -> {
                            if("japanese".equalsIgnoreCase(language)) {
                                String infinitive = furiganaService.addFurigana(aiConjugationExample.infinitive());
                                String conjugatedForm = furiganaService.addFurigana(aiConjugationExample.conjugatedForm());
                                String exampleSentence = furiganaService.addFurigana(aiConjugationExample.exampleSentence());
                                return ConjugationExampleDTO.builder()
                                        .conjugatedForm(conjugatedForm)
                                        .infinitive(infinitive)
                                        .exampleSentence(exampleSentence)
                                        .sentenceTranslation(aiConjugationExample.sentenceTranslation())
                                        .build();
                            }
                            return ConjugationExampleDTO.builder()
                                    .conjugatedForm(aiConjugationExample.conjugatedForm())
                                    .infinitive(aiConjugationExample.infinitive())
                                    .exampleSentence(aiConjugationExample.exampleSentence())
                                    .sentenceTranslation(aiConjugationExample.sentenceTranslation())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public PracticeLessonDTO toPracticeLessonDTO(AIPracticeLessonResponse response, String language) {
        return PracticeLessonDTO.builder()
                .title(response.title())
                .instructions(response.instructions())
                .questions(response.questions().stream()
                        .filter(aiQuestion -> aiQuestion.questionText() != null && !aiQuestion.questionText().isBlank())
                        .map(aiQuestion -> {
                            String questionText = "japanese".equalsIgnoreCase(language)
                                    ? furiganaService.addFurigana(aiQuestion.questionText())
                                    : aiQuestion.questionText();
                            return QuestionDTO.builder()
                                    .questionText(questionText)
                                    .questionType(QuestionType.FREE_FORM.name())
                                    .answer(null)
                                    .answerChoices(Collections.emptyList())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public ReadingComprehensionLessonDTO toReadingComprehensionLessonDTO(AIReadingComprehensionLessonResponse response, String language) {
        String story = "japanese".equalsIgnoreCase(language)
                ? furiganaService.addFurigana(response.story())
                : response.story();
        return ReadingComprehensionLessonDTO.builder()
                .title(response.title())
                .story(story)
                .questions(response.questions().stream()
                        .filter(aiQuestion -> aiQuestion.questionText() != null && !aiQuestion.questionText().isBlank())
                        .map(aiQuestion -> {
                            return QuestionDTO.builder()
                                    .questionText(aiQuestion.questionText())
                                    .questionType(QuestionType.MULTIPLE_CHOICE.name())
                                    .answer(aiQuestion.answer())
                                    .answerChoices(aiQuestion.answerChoices())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public PracticeLessonCheckResponse toPracticeLessonCheckResponse(AIProofreadResponse response) {
        return PracticeLessonCheckResponse.builder()
                .isCorrect(response.correctedSentence() == null)
                .correctedSentence(response.correctedSentence())
                .feedback(response.feedback())
                .build();
    }

}