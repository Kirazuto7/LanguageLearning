package com.example.language_learning.repositories;

import com.example.language_learning.entity.languages.JapaneseWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JapaneseWordRepository extends JpaRepository<JapaneseWord, Long> {
    // Example of a language-specific query
    Optional<JapaneseWord> findByHiragana(String hiragana);
}