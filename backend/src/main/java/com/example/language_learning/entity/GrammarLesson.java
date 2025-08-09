package com.example.language_learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "grammar_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrammarLesson extends Lesson{
    private String grammarConcept;
    private String explanation;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "grammar_lesson_id")
    private List<Sentence> examples;

}
