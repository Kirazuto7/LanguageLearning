package com.example.language_learning.lessonbook.chapter.lesson.page.question.data;

import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.shared.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "question_answer_choices", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer_choice", columnDefinition = "TEXT")
    @Default
    private List<String> answerChoices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonBackReference("lesson-questions")
    private Lesson lesson;

    public Question(QuestionType questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
}
