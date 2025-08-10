package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Question;
import com.example.language_learning.entity.models.Sentence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "practice_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PracticeLesson extends Lesson {

    @Column(columnDefinition = "TEXT")
    private String instructions; // e.g., "Use the vocabulary you've learned to complete the exercises."

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setLesson(this);
    }
}
