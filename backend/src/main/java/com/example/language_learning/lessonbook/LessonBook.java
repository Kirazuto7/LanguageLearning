package com.example.language_learning.lessonbook;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.shared.data.BaseBook;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lesson_books")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LessonBook extends BaseBook {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lessonBook", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @Builder.Default
    private List<LessonChapter> lessonChapters = new ArrayList<>();

    public void addLessonChapter(LessonChapter lessonChapter) {
        lessonChapters.add(lessonChapter);
        lessonChapter.setLessonBook(this);
    }
}
