package com.example.language_learning.entity.models;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_books")
@Builder
@Data
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
    private List<Chapter> chapters = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
        chapter.setLessonBook(this);
    }
}
