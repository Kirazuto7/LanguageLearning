package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExample;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conjugation_lessons")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConjugationLesson extends Lesson {
    // The name of the rule being taught, e.g., "Present Tense"
    private String conjugationRuleName;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
            name = "conjugation_lesson_examples",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "example_id")
    )
    @OrderBy("id ASC")
    @Builder.Default
    private List<LessonConjugationExample> conjugatedWords = new ArrayList<>();

    public void addConjugationExample(LessonConjugationExample example) {
        this.conjugatedWords.add(example);
    }
}
