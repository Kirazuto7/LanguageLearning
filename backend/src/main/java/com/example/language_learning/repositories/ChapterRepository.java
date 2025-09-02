package com.example.language_learning.repositories;

import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByIdAndLessonBook_User(Long id, User user);
}

