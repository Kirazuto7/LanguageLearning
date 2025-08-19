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

@Service
@RequiredArgsConstructor
public class BookService {
    private final UserRepository userRepository;
    private final LessonBookRepository lessonBookRepository;
    private final DtoMapper dtoMapper;

    public LessonBook findOrCreateBook(String language, String difficulty, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return lessonBookRepository.findByUserAndLanguageAndDifficulty(user, language, difficulty)
                .orElseGet(() -> {
                    LessonBook newBook = new LessonBook();
                    newBook.setBookTitle(String.format("%s for %s learners", language, difficulty));
                    newBook.setLanguage(language);
                    newBook.setDifficulty(difficulty);
                    newBook.setUser(user);
                    return lessonBookRepository.save(newBook);
                });
    }

    public LessonBookDTO fetchBook(LessonBookRequest request) {
        LessonBook book = findOrCreateBook(request.language(), request.difficulty(), request.userId());
        return dtoMapper.toDto(book);
    }

    public LessonBook save(LessonBook book) {
        return lessonBookRepository.save(book);
    }
}
