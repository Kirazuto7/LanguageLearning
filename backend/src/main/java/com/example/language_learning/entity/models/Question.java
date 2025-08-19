package com.example.language_learning.entity.models;

import com.example.language_learning.entity.lessons.Lesson;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String answer;

    // For multiple choice, a list of options as answer choices. Otherwise, null.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option", columnDefinition = "TEXT")
    @Default
    private List<String> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Question(QuestionType questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
}
