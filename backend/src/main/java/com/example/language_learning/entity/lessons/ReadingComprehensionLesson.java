package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_comprehension_lessons")
@Getter
@Setter
@NoArgsConstructor
public class ReadingComprehensionLesson extends Lesson {
    @Column(columnDefinition = "TEXT")
    private String story;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "question_order")
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setLesson(this);
    }
}
