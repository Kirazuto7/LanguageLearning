package com.example.language_learning.entity.models;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(columnDefinition = "TEXT")
    private String details; // JSON string meant to hold language-specific metadata e.g, hiragana, katakana, romaji
}
