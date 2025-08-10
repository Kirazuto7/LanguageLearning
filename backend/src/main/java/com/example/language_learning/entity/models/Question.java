package com.example.language_learning.entity.models;

import com.example.language_learning.entity.lessons.Lesson;
import com.example.language_learning.entity.lessons.PracticeLesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionType; // e.g., "multiple_choice", "fill_in_the_blank", "FREE_FORM"

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String answer;

    // For multiple choice, a list of options as answer choices. Otherwise, null.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option")
    private List<String> options;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Question(String questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
}
