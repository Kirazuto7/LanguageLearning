package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Page;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lessons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LessonType type;
    private String title;

    @OneToOne(mappedBy = "lesson")
    private Page page;
}
