package com.example.language_learning.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@Table(name = "vocabulary_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;
    private String translation;
}
