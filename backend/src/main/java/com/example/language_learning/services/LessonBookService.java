package com.example.language_learning.services;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.LessonBookRepository;
import com.example.language_learning.requests.LessonBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonBookService {
    private final LessonBookRepository lessonBookRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public LessonBookDTO findOrCreateBookDTO(LessonBookRequest request, User user) {
        LessonBook lessonBook = findOrCreateBook(request.language(), request.difficulty(), user);
        return dtoMapper.toDto(lessonBook);
    }

    @Transactional
    public LessonBook findOrCreateBook(String language, String difficulty, User user) {
        return getLessonBook(language, difficulty, user)
                .orElseGet(() -> createLessonBook(language, difficulty, user));
    }

    @Transactional(readOnly = true)
    public List<LessonBookDTO> fetchUserLessonBooks(User user) {
        return lessonBookRepository.findAllByUser(user).stream()
                .map(dtoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<LessonBook> getLessonBook(String language, String difficulty, User user) {
        return lessonBookRepository.findByUserAndLanguageAndDifficulty(user, language, difficulty);
    }

    @Transactional
    public LessonBook createLessonBook(String language, String difficulty, User user) {
        LessonBook newBook = LessonBook.builder()
                .bookTitle(String.format("%s for %s Learners", language, difficulty))
                .language(language)
                .difficulty(difficulty)
                .user(user)
                .build();
        return lessonBookRepository.save(newBook);
    }

    @Transactional
    public LessonBook save(LessonBook book) {
        return lessonBookRepository.save(book);
    }
}
