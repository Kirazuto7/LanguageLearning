package com.example.language_learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "sentence_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentenceLesson extends Lesson{
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sentence_lesson_id")
    private List<Sentence> sentences;
}
