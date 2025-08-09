package com.example.language_learning.entity.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name="sentences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "sentence",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("wordIndex ASC")
    private List<SentenceWord> words;
    private String translation; // English translation of the entire sentence
}
