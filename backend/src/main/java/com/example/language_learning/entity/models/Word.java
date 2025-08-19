package com.example.language_learning.entity.models;

import com.example.language_learning.mapper.util.JpaMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String englishTranslation; // English translation

    @Column(nullable = false)
    private String language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nativeWord; // e.g., 寿司, 음식

    @Column(columnDefinition = "TEXT")
    private String phoneticSpelling; // e.g., すし, eum-sik

    @Convert(converter = JpaMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> details; // contains language-specific metadata e.g, hiragana, katakana, romaji
}
