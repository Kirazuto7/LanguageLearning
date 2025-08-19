package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.ConjugationExample;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConjugationExample> conjugatedWords = new ArrayList<>();

    public void addConjugationExample(ConjugationExample example) {
        this.conjugatedWords.add(example);
        example.setLesson(this);
    }
}
