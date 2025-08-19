package com.example.language_learning.entity.models;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "chapters")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int chapterNumber;
    private String title;
    private String nativeTitle;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "chapter")
    @OrderColumn(name = "page_order")
    @Builder.Default
    private List<Page> pages = new ArrayList<>();

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private LessonBook lessonBook;

    public void addPage(Page page) {
        pages.add(page);
        page.setChapter(this);
    }
}
