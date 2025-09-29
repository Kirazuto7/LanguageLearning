package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.shared.data.BasePage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lesson_pages")
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LessonPage extends BasePage {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_chapter_id")
    @JsonBackReference
    private LessonChapter lessonChapter;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lesson_id")
    @JsonManagedReference("lesson-page")
    private Lesson lesson;
}
