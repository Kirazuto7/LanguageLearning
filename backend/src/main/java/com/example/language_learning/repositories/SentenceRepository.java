package com.example.language_learning.repositories;

import com.example.language_learning.entity.models.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
}