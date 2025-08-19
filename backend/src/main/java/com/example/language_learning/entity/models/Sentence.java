package com.example.language_learning.entity.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="sentences")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String translation; // English translation of the entire sentence

}
