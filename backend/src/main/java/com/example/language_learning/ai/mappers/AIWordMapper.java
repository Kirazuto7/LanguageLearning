package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.details.*;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.*;
import com.example.language_learning.shared.services.FuriganaService;
import com.example.language_learning.shared.utils.AIResponseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIWordMapper {
    private final FuriganaService furiganaService;
    private final AIResponseSanitizer sanitizer;

    public WordDTO toWordDTO(Object aiVocab, String language) {
        if (aiVocab == null) return null;

        return switch (aiVocab) {
            case AIJapaneseVocabularyItemDTO j -> toWordDTO(j, language);
            case AIKoreanVocabularyItemDTO k -> toWordDTO(k, language);
            case AIChineseVocabularyItemDTO c -> toWordDTO(c, language);
            case AIThaiVocabularyItemDTO t -> toWordDTO(t, language);
            case AIItalianVocabularyItemDTO i -> toWordDTO(i, language);
            case AISpanishVocabularyItemDTO s -> toWordDTO(s, language);
            case AIFrenchVocabularyItemDTO f -> toWordDTO(f, language);
            case AIGermanVocabularyItemDTO g -> toWordDTO(g, language);
            default -> null;
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
