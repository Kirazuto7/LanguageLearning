package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentence;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference("lesson-exampleSentences")
    private List<LessonSentence> exampleLessonSentences = new ArrayList<>();

    public void addExampleSentence(LessonSentence lessonSentence) {
        this.exampleLessonSentences.add(lessonSentence);
    }
}
