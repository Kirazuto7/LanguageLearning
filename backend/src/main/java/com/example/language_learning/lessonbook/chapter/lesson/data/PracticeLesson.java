package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestion;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @Builder.Default
    @JsonManagedReference("lesson-lessonQuestions")
    private List<LessonQuestion> lessonQuestions = new ArrayList<>();

    public void addQuestion(LessonQuestion lessonQuestion) {
        this.lessonQuestions.add(lessonQuestion);
        lessonQuestion.setLesson(this);
    }
}
