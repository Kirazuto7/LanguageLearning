package com.example.language_learning.entity.lessons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.language_learning.entity.languages.Word;
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
public class VocabularyLesson extends Lesson {

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyWord> vocabularies = new ArrayList<>();


    public List<VocabularyWord> getVocabularies() {
        return Collections.unmodifiableList(vocabularies);
    }

    public void addWord(Word word, int index) {
        VocabularyWord vocabularyWord = new VocabularyWord(this, word, index);
        vocabularies.add(vocabularyWord);
    }
}
