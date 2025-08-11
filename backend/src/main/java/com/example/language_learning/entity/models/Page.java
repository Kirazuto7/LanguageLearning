package com.example.language_learning.entity.models;

import com.example.language_learning.entity.lessons.Lesson;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pageNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    @JsonBackReference
    private Chapter chapter;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public Page(int pageNumber, Lesson lesson) {
        this.pageNumber = pageNumber;
        this.lesson = lesson;
        if(lesson != null) {
            lesson.setPage(this);
        }
    }
}
