package com.example.language_learning.entity.models;

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
}
