package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.shared.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name="sentences")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonSentence extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String translation; // English translation of the entire sentence

}
