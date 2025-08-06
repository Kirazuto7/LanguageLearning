package com.example.language_learning.repositories;

import com.example.language_learning.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByLanguageAndDifficulty(String language, String difficulty);
}

