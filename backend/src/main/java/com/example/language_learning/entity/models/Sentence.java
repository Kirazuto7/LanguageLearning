package com.example.language_learning.entity.models;

import com.example.language_learning.entity.languages.Word;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name="sentences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "sentence",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("wordIndex ASC")
    private List<SentenceWord> words;
    private String text;
    private String translation; // English translation of the entire sentence

    public void addWord(SentenceWord sentenceWord) {
        words.add(sentenceWord);
        sentenceWord.setSentence(this);
    }

    public void addWord(Word word) {
        SentenceWord sentenceWord = new SentenceWord();
        sentenceWord.setSentence(this);
        sentenceWord.setWord(word);
        sentenceWord.setWordIndex(this.words.size());
        this.words.add(sentenceWord);
    }
}
