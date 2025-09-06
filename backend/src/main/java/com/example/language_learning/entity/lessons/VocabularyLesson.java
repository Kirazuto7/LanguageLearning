package com.example.language_learning.entity.lessons;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.entity.models.Word;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
            name = "vocabulary_lesson_words",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @OrderBy("id ASC")
    @Builder.Default
    private List<Word> vocabularies = new ArrayList<>();

}
