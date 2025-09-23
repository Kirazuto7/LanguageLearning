package com.example.language_learning.lessonbook.chapter.lesson.data;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.shared.word.data.Word;
import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "vocabulary_lessons")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyLesson extends Lesson {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "vocabulary_lesson_words",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @OrderBy("id ASC")
    @Builder.Default
    private List<Word> vocabularies = new ArrayList<>();

}
