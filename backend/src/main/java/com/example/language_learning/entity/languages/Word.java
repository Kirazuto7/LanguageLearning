package com.example.language_learning.entity.languages;

import com.example.language_learning.entity.lessons.VocabularyLesson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
}
