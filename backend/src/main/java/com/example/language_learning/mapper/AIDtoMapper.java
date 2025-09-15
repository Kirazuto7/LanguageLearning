package com.example.language_learning.mapper;

import com.example.language_learning.ai.dtos.*;
import com.example.language_learning.ai.dtos.details.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.enums.QuestionType;
import com.example.language_learning.responses.TranslationResponse;
import com.example.language_learning.utils.AIResponseSanitizer;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.FuriganaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maps from the API-Response DTOs (sanitized) to the
 * application's internal DTOs.
 */
@Component
@RequiredArgsConstructor
public class AIDtoMapper {

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
                        .map(aiVocab -> toWordDTO(aiVocab, language)) // Polymorphic dispatch
                        .filter(Objects::nonNull)
                        .toList())
                .build();
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response, String language) {
        return GrammarLessonDTO.builder()
                .title(response.title())
                .grammarConcept(response.grammarConcept())
                .explanation(response.explanation())
                .exampleSentences(response.exampleSentences().stream()
                        .filter(aiSentence -> aiSentence.text() != null && !aiSentence.text().isBlank())
                        .map(aiSentence -> {
                            String text = "japanese".equalsIgnoreCase(language)
                                    ? furiganaService.addFurigana(aiSentence.text())
                                    : aiSentence.text();
                            return SentenceDTO.builder()
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
                                return ConjugationExampleDTO.builder()
                                        .conjugatedForm(conjugatedForm)
                                        .infinitive(infinitive)
                                        .exampleSentence(exampleSentence)
                                        .sentenceTranslation(sanitizer.sanitizeEnglishSentence(aiConjugationExample.sentenceTranslation()))
                                        .build();
                            }
                            return ConjugationExampleDTO.builder()
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

                            return QuestionDTO.builder()
                                    .questionText(questionText)
                                    .questionType(QuestionType.MULTIPLE_CHOICE.name())
                                    .answer(answer)
                                    .answerChoices(answerChoices)
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

    public TranslationResponse toTranslationResponse(AITranslationResponse response) {
        return TranslationResponse.builder()
                .translatedText(response.translatedText())
                .build();
    }

    private WordDTO toWordDTO(Object aiVocab, String language) {
        return switch (aiVocab) {
            case AIJapaneseVocabularyItemDTO j -> toWordDTO(j, language);
            case AIKoreanVocabularyItemDTO k -> toWordDTO(k, language);
            case AIChineseVocabularyItemDTO c -> toWordDTO(c, language);
            case AIThaiVocabularyItemDTO t -> toWordDTO(t, language);
            case AIItalianVocabularyItemDTO i -> toWordDTO(i, language);
            case AISpanishVocabularyItemDTO s -> toWordDTO(s, language);
            case AIFrenchVocabularyItemDTO f -> toWordDTO(f, language);
            case AIGermanVocabularyItemDTO g -> toWordDTO(g, language);
            default -> null; // Or throw an exception
        };
    }

    private WordDTO toWordDTO(AIJapaneseVocabularyItemDTO aiVocab, String language) {
        JapaneseWordDetailsDTO details = furiganaService.verifyAndMapJapaneseWord(aiVocab);

        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIKoreanVocabularyItemDTO aiVocab, String language) {
        KoreanWordDetailsDTO details = KoreanWordDetailsDTO.builder()
                .hangul(aiVocab.hangul())
                .hanja(aiVocab.hanja())
                .romaja(aiVocab.romaja())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIChineseVocabularyItemDTO aiVocab, String language) {
        ChineseWordDetailsDTO details = ChineseWordDetailsDTO.builder()
                .simplified(aiVocab.simplified())
                .traditional(aiVocab.traditional())
                .pinyin(aiVocab.pinyin())
                .toneNumber(aiVocab.toneNumber())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIThaiVocabularyItemDTO aiVocab, String language) {
        ThaiWordDetailsDTO details = ThaiWordDetailsDTO.builder()
                .thaiScript(aiVocab.thaiScript())
                .romanization(aiVocab.romanization())
                .tonePattern(aiVocab.tonePattern())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIItalianVocabularyItemDTO aiVocab, String language) {
        ItalianWordDetailsDTO details = ItalianWordDetailsDTO.builder()
                .lemma(aiVocab.lemma())
                .gender(aiVocab.gender())
                .pluralForm(aiVocab.pluralForm())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIGermanVocabularyItemDTO aiVocab, String language) {
        GermanWordDetailsDTO details = GermanWordDetailsDTO.builder()
                .lemma(aiVocab.lemma())
                .gender(aiVocab.gender())
                .pluralForm(aiVocab.pluralForm())
                .separablePrefix(aiVocab.separablePrefix())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AIFrenchVocabularyItemDTO aiVocab, String language) {
        FrenchWordDetailsDTO details = FrenchWordDetailsDTO.builder()
                .lemma(aiVocab.lemma())
                .gender(aiVocab.gender())
                .pluralForm(aiVocab.pluralForm())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    private WordDTO toWordDTO(AISpanishVocabularyItemDTO aiVocab, String language) {
        SpanishWordDetailsDTO details = SpanishWordDetailsDTO.builder()
                .lemma(aiVocab.lemma())
                .gender(aiVocab.gender())
                .pluralForm(aiVocab.pluralForm())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }
}