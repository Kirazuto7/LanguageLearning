package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.Sentence;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grammar_lessons")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrammarLesson extends Lesson {
    @Column(columnDefinition = "TEXT")
    private String grammarConcept;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
            name = "grammar_lesson_sentences",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "sentence_id")
    )
    @OrderBy("id ASC")
    @Builder.Default
    private List<Sentence> exampleSentences = new ArrayList<>();

    public void addExampleSentence(Sentence sentence) {
        this.exampleSentences.add(sentence);
    }
}
