package com.example.language_learning.entity.models;
import java.util.List;

import com.example.language_learning.entity.lessons.Lesson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table(name = "chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int chapterNumber;
    private String title;
    private String nativeTitle;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "chapter")
    @OrderColumn(name = "page_order")
    private List<Page> pages;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private LessonBook lessonBook;

}
