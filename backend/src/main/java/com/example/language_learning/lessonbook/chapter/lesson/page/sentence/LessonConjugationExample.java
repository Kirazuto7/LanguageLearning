package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.shared.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "conjugation_examples")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonConjugationExample extends BaseEntity {
    private String infinitive; // Dictionary Form aka base form

    @Column(columnDefinition = "TEXT")
    private String conjugatedForm;

    @Column(columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(columnDefinition = "TEXT")
    private String sentenceTranslation;
}
