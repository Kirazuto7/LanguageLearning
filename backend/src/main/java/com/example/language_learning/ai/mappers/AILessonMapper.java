package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.lessonbook.*;
import com.example.language_learning.lessonbook.chapter.ChapterMetadataDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExampleDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentenceDTO;
import com.example.language_learning.shared.enums.QuestionType;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.*;
import com.example.language_learning.shared.services.FuriganaService;
import com.example.language_learning.shared.utils.AIResponseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AILessonMapper {

    private final AIWordMapper aiWordMapper;
    private final FuriganaService furiganaService;
    private final AIResponseSanitizer sanitizer;

    public ChapterMetadataDTO toChapterMetadataDTO(AIChapterMetadataResponse response, String topic) {
        return ChapterMetadataDTO.builder()
                .nativeTitle(response.nativeTitle())
                .title(response.title())
                .topic(topic)
                .build();
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIVocabularyLessonResponse<?> response, String language) {
        return VocabularyLessonDTO.builder()
                .title(response.title())
                .vocabularies(response.vocabularies().stream()
                        .filter(Objects::nonNull)
                        .map(aiVocab -> aiWordMapper.toWordDTO(aiVocab, language)) // Polymorphic dispatch
                        .filter(Objects::nonNull)
                        .toList())
                .build();
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response, String language) {
        return GrammarLessonDTO.builder()
                .title(response.title())
                .grammarConcept(response.grammarConcept())
                .explanation(response.explanation())
                .exampleLessonSentences(response.exampleSentences().stream()
                        .filter(aiSentence -> aiSentence.text() != null && !aiSentence.text().isBlank())
                        .map(aiSentence -> {
                            String text = "japanese".equalsIgnoreCase(language)
                                    ? furiganaService.addFurigana(aiSentence.text())
                                    : aiSentence.text();
                            return LessonSentenceDTO.builder()
                                    .text(text)
                                    .translation(sanitizer.sanitizeEnglishSentence(aiSentence.translation()))
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
                                return LessonConjugationExampleDTO.builder()
                                        .conjugatedForm(conjugatedForm)
                                        .infinitive(infinitive)
                                        .exampleSentence(exampleSentence)
                                        .sentenceTranslation(sanitizer.sanitizeEnglishSentence(aiConjugationExample.sentenceTranslation()))
                                        .build();
                            }
                            return LessonConjugationExampleDTO.builder()
                                    .conjugatedForm(aiConjugationExample.conjugatedForm())
                                    .infinitive(aiConjugationExample.infinitive())
                                    .exampleSentence(aiConjugationExample.exampleSentence())
                                    .sentenceTranslation(sanitizer.sanitizeEnglishSentence(aiConjugationExample.sentenceTranslation()))
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }

    public PracticeLessonDTO toPracticeLessonDTO(AIPracticeLessonResponse response, String language) {
        return PracticeLessonDTO.builder()
                .title(response.title())
                .instructions(response.instructions())
                .lessonQuestions(response.questions().stream()
                        .filter(aiQuestion -> aiQuestion.questionText() != null && !aiQuestion.questionText().isBlank())
                        .map(aiQuestion -> {
                            String questionText = "japanese".equalsIgnoreCase(language)
                                    ? furiganaService.addFurigana(aiQuestion.questionText())
                                    : aiQuestion.questionText();
                            return LessonQuestionDTO.builder()
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
                .lessonQuestions(response.questions().stream()
                        .filter(aiQuestion -> aiQuestion.questionText() != null && !aiQuestion.questionText().isBlank())
                        .map(aiQuestion -> {
                            String questionText;
                            String answer;
                            List<String> answerChoices;

                            if ("japanese".equalsIgnoreCase(language)) {
                                questionText = furiganaService.addFurigana(aiQuestion.questionText());
                                answer = furiganaService.addFurigana(aiQuestion.answer());
                                answerChoices = aiQuestion.answerChoices() != null
                                        ? aiQuestion.answerChoices().stream().map(furiganaService::addFurigana).collect(Collectors.toList())
                                        : Collections.emptyList();
                            } else {
                                questionText = aiQuestion.questionText();
                                answer = aiQuestion.answer();
                                answerChoices = aiQuestion.answerChoices() != null ? aiQuestion.answerChoices() : Collections.emptyList();
                            }

                            return LessonQuestionDTO.builder()
                                    .questionText(questionText)
                                    .questionType(QuestionType.MULTIPLE_CHOICE.name())
                                    .answer(answer)
                                    .answerChoices(answerChoices)
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }
}