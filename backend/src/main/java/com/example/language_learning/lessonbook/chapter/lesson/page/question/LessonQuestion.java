package com.example.language_learning.lessonbook.chapter.lesson.page.question;

import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.shared.data.BaseEntity;
import com.example.language_learning.shared.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessonQuestions")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonQuestion extends BaseEntity {
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
    @JsonBackReference("lesson-lessonQuestions")
    private Lesson lesson;

    public LessonQuestion(QuestionType questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
}
