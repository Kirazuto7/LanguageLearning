package com.example.language_learning.lessonbook.chapter.lesson.data;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.shared.data.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.language_learning.shared.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "lessons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LessonType type;
    private String title;

    @OneToOne(mappedBy = "lesson")
    @JsonBackReference("lesson-page")
    private LessonPage lessonPage;
}
