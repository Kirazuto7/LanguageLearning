package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.ConjugationExample;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String infinitive; // Base verb/adjective aka dictionary form

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConjugationExample> conjugationTable = new ArrayList<>();

    public void addConjugationExample(ConjugationExample example) {
        this.conjugationTable.add(example);
        example.setLesson(this);
    }
}
