package com.example.language_learning.repositories;

import com.example.language_learning.entity.models.MultipleChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultipleChoiceRepository extends JpaRepository<MultipleChoice, Long> {
}