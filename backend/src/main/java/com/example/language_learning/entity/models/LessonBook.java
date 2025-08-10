package com.example.language_learning.entity.models;

import java.util.List;

import com.example.language_learning.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lesson_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookTitle;
    private String difficulty;
    private String language;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lessonBook")
    @OrderColumn(name = "chapter_order")
    private List<Chapter> chapters;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
        chapter.setLessonBook(this);
    }
}
