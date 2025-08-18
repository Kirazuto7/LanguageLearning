package com.example.language_learning.entity.lessons;

import java.util.ArrayList;
import java.util.List;

import com.example.language_learning.entity.languages.Word;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "vocabulary_lessons")
@Getter
@Setter
@NoArgsConstructor
public class VocabularyLesson extends Lesson {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "vocabulary_lesson_words",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @OrderColumn(name = "word_order")
    private List<Word> vocabularies = new ArrayList<>();

}
