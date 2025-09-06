package com.example.language_learning.services;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.LessonBookRepository;
import com.example.language_learning.repositories.UserRepository;
import com.example.language_learning.requests.LessonBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonBookService {
    private final UserRepository userRepository;
    private final LessonBookRepository lessonBookRepository;
    private final DtoMapper dtoMapper;

    public LessonBook findOrCreateBook(String language, String difficulty, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return lessonBookRepository.findByUserAndLanguageAndDifficulty(user, language, difficulty)
                .orElseGet(() -> {
                    LessonBook newBook = LessonBook.builder()
                            .bookTitle(String.format("%s for %s Learners", language, difficulty))
                            .language(language)
                            .difficulty(difficulty)
                            .user(user)
                            .build();
                    return lessonBookRepository.save(newBook);
                });
    }

    @Transactional(readOnly = true)
    public List<LessonBookDTO> fetchUserLessonBooks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return lessonBookRepository.findAllByUser(user).stream()
                .map(dtoMapper::toDto)
                .toList();
    }

    @Transactional
    public LessonBookDTO fetchLessonBook(LessonBookRequest request, Long userId) {
        LessonBook book = findOrCreateBook(request.language(), request.difficulty(), userId);
        return dtoMapper.toDto(book);
    }

    public LessonBook save(LessonBook book) {
        return lessonBookRepository.save(book);
    }
}
