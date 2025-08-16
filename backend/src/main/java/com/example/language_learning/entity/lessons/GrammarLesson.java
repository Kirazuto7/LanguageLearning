package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Sentence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grammar_lessons")
@Getter
@Setter
@NoArgsConstructor
public class GrammarLesson extends Lesson {
    @Column(columnDefinition = "TEXT")
    private String grammarConcept;

    @Column(columnDefinition = "TEXT")
    private String nativeGrammarConcept;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "grammar_lesson_sentences",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "sentence_id")
    )
    @OrderColumn(name = "sentence_order")
    private List<Sentence> exampleSentences = new ArrayList<>();

    public void addExampleSentence(Sentence sentence) {
        this.exampleSentences.add(sentence);
    }
}
