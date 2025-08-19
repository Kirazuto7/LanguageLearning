package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Question;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "practice_lessons")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PracticeLesson extends Lesson {

    @Column(columnDefinition = "TEXT")
    private String instructions; // e.g., "Use the vocabulary you've learned to complete the exercises."

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setLesson(this);
    }
}
