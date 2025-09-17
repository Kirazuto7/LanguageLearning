package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.ConjugationExample;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
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

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @Builder.Default
    private List<ConjugationExample> conjugatedWords = new ArrayList<>();

    public void addConjugationExample(ConjugationExample example) {
        this.conjugatedWords.add(example);
        example.setLesson(this);
    }
}
