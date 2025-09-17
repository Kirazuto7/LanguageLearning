package com.example.language_learning.lessonbook.data;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.lessonbook.chapter.data.Chapter;
import com.example.language_learning.user.data.User;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lessonBook", fetch = FetchType.LAZY)
    @OrderBy("chapterNumber ASC")
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
        chapter.setLessonBook(this);
    }
}
