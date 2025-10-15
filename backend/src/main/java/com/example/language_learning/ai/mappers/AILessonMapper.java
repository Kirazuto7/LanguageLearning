package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.services.AIValidationService;
import com.example.language_learning.ai.dtos.lessonbook.*;
import com.example.language_learning.lessonbook.chapter.ChapterMetadataDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExampleDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentenceDTO;
import com.example.language_learning.shared.enums.QuestionType;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.*;
import com.example.language_learning.shared.services.FuriganaService;
import com.example.language_learning.shared.utils.AIResponseSanitizer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AILessonMapper {

    private final AIWordMapper aiWordMapper;
    private final FuriganaService furiganaService;
    private final AIResponseSanitizer sanitizer;
    private final AIValidationService aiValidationService;

    public AILessonMapper(AIWordMapper aiWordMapper, FuriganaService furiganaService, AIResponseSanitizer sanitizer, Optional<AIValidationService> aiValidationService) {
        this.aiWordMapper = aiWordMapper;
        this.furiganaService = furiganaService;
        this.sanitizer = sanitizer;
        this.aiValidationService = aiValidationService.orElse(null);
    }

    public ChapterMetadataDTO toChapterMetadataDTO(AIChapterMetadataResponse response, String topic) {
        return ChapterMetadataDTO.builder()
                .nativeTitle(response.nativeTitle())
                .title(response.title())
                .topic(topic)
                .build();
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIVocabularyLessonResponse<?> response, String language) {
        Set<String> seenWords = new HashSet<>();
        return VocabularyLessonDTO.builder()
                .title(response.title())
                .vocabularies(response.vocabularies().stream()
                        .filter(Objects::nonNull)
                        .map(aiVocab -> aiWordMapper.toWordDTO(aiVocab, language)) // Polymorphic dispatch
                        .filter(Objects::nonNull)
                        // Filter out duplicates based on the original word (e.g., hangul, kanji, lemma).
                        .filter(wordDTO -> {
                            String originalWord = aiWordMapper.getOriginalWordFromDTO(wordDTO);
                            return originalWord != null && seenWords.add(originalWord);
                        })
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
                            String validatedAnswer;
                            List<String> answerChoices;

                            if ("japanese".equalsIgnoreCase(language)) {
                                questionText = furiganaService.addFurigana(aiQuestion.questionText());
                                String rawAnswer = furiganaService.addFurigana(aiQuestion.answer());
                                answerChoices = aiQuestion.answerChoices() != null
                                        ? aiQuestion.answerChoices().stream().map(furiganaService::addFurigana).collect(Collectors.toList())
                                        : Collections.emptyList();
                                // Skip validation if the service is disabled
                                validatedAnswer = (aiValidationService != null)
                                        ? aiValidationService.findBestMatchingAnswer(rawAnswer, answerChoices)
                                        : rawAnswer;
                            } else {
                                questionText = aiQuestion.questionText();
                                answerChoices = aiQuestion.answerChoices() != null ? aiQuestion.answerChoices() : Collections.emptyList();
                                // Skip validation if the service is disabled
                                validatedAnswer = (aiValidationService != null)
                                        ? aiValidationService.findBestMatchingAnswer(aiQuestion.answer(), answerChoices)
                                        : aiQuestion.answer();
                            }

                            return LessonQuestionDTO.builder()
                                    .questionText(questionText)
                                    .questionType(QuestionType.MULTIPLE_CHOICE.name())
                                    .answer(validatedAnswer)
                                    .answerChoices(answerChoices)
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }
}