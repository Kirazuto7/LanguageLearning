package com.example.language_learning.dto.languages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JapaneseWordDTO extends WordDTO {
    private String hiragana;
    private String katakana;
    private String kanji;
    private String romaji;

    @Override
    public String getPrimaryRepresentation() {
        // Prioritize the most complete representation of the word.
        // 1. Kanji (which may include Hiragana, e.g., 食べる)
        // 2. Katakana (for loan words)
        // 3. Hiragana (for words with no Kanji)
        if (kanji != null && !kanji.trim().isEmpty()) {
            return getKanji();
        }
        if (katakana != null && !katakana.trim().isEmpty()) {
            return getKatakana();
        }
        if (hiragana != null && !hiragana.trim().isEmpty()) {
            return getHiragana();
        }
        if (romaji != null && !romaji.trim().isEmpty()) {
            return getRomaji();
        }
        return "";
    }
}
