package com.example.language_learning.entity;

import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name ="vocabulary_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyLesson extends Lesson {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vocabulary_lesson_id")
    private List<VocabularyItem> vocabularyItems;

   

}
