package com.example.language_learning.repositories;

import com.example.language_learning.entity.languages.KoreanWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KoreanWordRepository extends JpaRepository<KoreanWord, Long> {
    // Example of a language-specific query
    Optional<KoreanWord> findByHangeul(String hangeul);
}