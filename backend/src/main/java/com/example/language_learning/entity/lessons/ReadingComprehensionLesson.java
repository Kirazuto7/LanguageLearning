package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.MultipleChoice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "reading_comprehension_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingComprehensionLesson extends Lesson {
    private String story;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reading_comprehension_lesson_id")
    @OrderColumn(name = "question_order")
    private List<MultipleChoice> questions;
}
