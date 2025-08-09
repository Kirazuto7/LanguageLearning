package com.example.language_learning.entity.lessons;

import java.util.List;

import com.example.language_learning.entity.models.Vocabulary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "vocabulary_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyLesson extends Lesson {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vocabulary_lesson_id")
    private List<Vocabulary> vocabularies;

}
