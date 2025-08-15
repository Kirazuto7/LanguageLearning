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
    private String explanation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "grammar_lesson_id")
    private List<Sentence> examples = new ArrayList<>();

    public void addExample(Sentence sentence) {
        this.examples.add(sentence);
    }
}
