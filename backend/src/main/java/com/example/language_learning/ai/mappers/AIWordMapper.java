package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.details.*;
import com.example.language_learning.ai.enums.Language;
import com.example.language_learning.shared.services.ChineseNlpService;
import com.example.language_learning.shared.services.KoreanNlpService;
import com.example.language_learning.shared.services.NlpService;
import com.example.language_learning.shared.word.dtos.*;
import com.example.language_learning.shared.services.FuriganaService;
import com.example.language_learning.shared.utils.AIResponseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIWordMapper {
    private final AIResponseSanitizer sanitizer;
    private final NlpService nlpService;
    private final KoreanNlpService koreanNlpService;
    private final FuriganaService furiganaService;
    private final ChineseNlpService chineseNlpService;

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
        String verifiedHanja = koreanNlpService.getHanja(aiVocab.hangul());
        KoreanWordDetailsDTO details = KoreanWordDetailsDTO.builder()
                .hangul(aiVocab.hangul())
                .hanja(verifiedHanja != null ? verifiedHanja : aiVocab.hanja())
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
                .traditional(chineseNlpService.verifyTraditional(aiVocab.simplified(), aiVocab.traditional()))
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
        String verifiedLemma = verifyLemma(aiVocab.lemma(), Language.ITALIAN);
        ItalianWordDetailsDTO details = ItalianWordDetailsDTO.builder()
                .lemma(verifiedLemma)
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
        String verifiedLemma = verifyLemma(aiVocab.lemma(), Language.GERMAN);
        GermanWordDetailsDTO details = GermanWordDetailsDTO.builder()
                .lemma(verifiedLemma)
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
        String verifiedLemma = verifyLemma(aiVocab.lemma(), Language.FRENCH);
        FrenchWordDetailsDTO details = FrenchWordDetailsDTO.builder()
                .lemma(verifiedLemma)
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
        String verifiedLemma = verifyLemma(aiVocab.lemma(), Language.SPANISH);
        SpanishWordDetailsDTO details = SpanishWordDetailsDTO.builder()
                .lemma(verifiedLemma)
                .gender(aiVocab.gender())
                .pluralForm(aiVocab.pluralForm())
                .build();
        return WordDTO.builder()
                .language(language)
                .englishTranslation(sanitizer.sanitizeEnglishSentence(aiVocab.englishTranslation()))
                .details(details)
                .build();
    }

    /**
     * A helper method to safely extract the "original" source word from a WordDTO
     * by inspecting its language-specific details record.
     *
     * @param wordDTO The WordDTO to inspect.
     * @return The original source word (e.g., kanji, hangul, lemma), or null if not found.
     */
    public String getOriginalWordFromDTO(WordDTO wordDTO) {
        if (wordDTO == null || wordDTO.details() == null) {
            return null;
        }

        return switch (wordDTO.details()) {
            case JapaneseWordDetailsDTO j -> j.kanji();
            case KoreanWordDetailsDTO k -> k.hangul();
            case ChineseWordDetailsDTO c -> c.simplified();
            case ThaiWordDetailsDTO t -> t.thaiScript();
            case ItalianWordDetailsDTO i -> i.lemma();
            case SpanishWordDetailsDTO s -> s.lemma();
            case FrenchWordDetailsDTO f -> f.lemma();
            case GermanWordDetailsDTO g -> g.lemma();
            default -> null;
        };
    }

    private String verifyLemma(String aiLemma, Language language) {
        String verifiedLemma = nlpService.getLemma(aiLemma, language);
        return verifiedLemma != null ? verifiedLemma : aiLemma;
    }
}
