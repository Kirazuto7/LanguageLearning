package com.example.language_learning.entity.lessons;

import java.util.List;

import com.example.language_learning.entity.models.VocabularyWord;
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

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyWord> vocabularies;

    public void addVocabulary(VocabularyWord vocabularyWord) {
        vocabularies.add(vocabularyWord);
        vocabularyWord.setLesson(this);
    }
}
