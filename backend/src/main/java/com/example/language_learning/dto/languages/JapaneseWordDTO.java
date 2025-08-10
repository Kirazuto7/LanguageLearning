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
}
