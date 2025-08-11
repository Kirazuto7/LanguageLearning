package com.example.language_learning.entity.languages;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "japanese_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JapaneseWord extends Word {
    private String hiragana;
    @Column(nullable = true)
    private String katakana;
    private String kanji;
    @Column(nullable = true)
    private String romaji;
}
