package com.example.language_learning.entity.languages;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "words")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String translation; // English translation
}
