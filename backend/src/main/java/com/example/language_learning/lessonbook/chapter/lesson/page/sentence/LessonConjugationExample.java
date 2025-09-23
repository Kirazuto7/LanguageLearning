package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.lessonbook.chapter.lesson.data.ConjugationLesson;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conjugation_examples")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonConjugationExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String infinitive; // Dictionary Form aka base form

    @Column(columnDefinition = "TEXT")
    private String conjugatedForm;

    @Column(columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(columnDefinition = "TEXT")
    private String sentenceTranslation;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private ConjugationLesson lesson;
}
