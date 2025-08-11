package com.example.language_learning.entity.models;

import com.example.language_learning.entity.languages.Word;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sentence_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentenceWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sentence_id")
    private Sentence sentence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "word_index", nullable = false)
    private int wordIndex;
}
