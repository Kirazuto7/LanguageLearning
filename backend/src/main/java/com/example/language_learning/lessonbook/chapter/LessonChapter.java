package com.example.language_learning.lessonbook.chapter;
import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.lessonbook.LessonBook;
import com.example.language_learning.shared.data.BaseChapter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;


@Entity
@Table(name = "lesson_chapters")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LessonChapter extends BaseChapter {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lessonChapter", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @BatchSize(size = 25)
    @Builder.Default
    private List<LessonPage> lessonPages = new ArrayList<>();

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private LessonBook lessonBook;

    public void addLessonPage(LessonPage lessonPage) {
        lessonPages.add(lessonPage);
        lessonPage.setLessonChapter(this);
    }
}
