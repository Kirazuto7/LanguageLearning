package com.example.language_learning.lessonbook;

import com.example.language_learning.user.User;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.lessonbook.requests.LessonBookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonBookService {
    private final LessonBookRepository lessonBookRepository;
    private final DtoMapper dtoMapper;

    public LessonBookDTO getLessonBookById(Long id, User user) {
        Optional<LessonBook> lessonBookOptional = lessonBookRepository.findDetailsById(id, user);
        return lessonBookOptional.map(dtoMapper::toDto).orElse(null);
    }

    @Transactional
    public LessonBookDTO findOrCreateBookDTO(LessonBookRequest request, User user) {
        LessonBook lessonBook = findOrCreateBook(request.language(), request.difficulty(), user);
        log.info("LessonBook: {}", lessonBook);
        return dtoMapper.toDto(lessonBook);
    }

    @Transactional
    public LessonBook findOrCreateBook(String language, String difficulty, User user) {
        Optional<LessonBook> lessonBookOptional = lessonBookRepository.findDetailsByUserAndLanguageAndDifficulty(user, language, difficulty);

        if (lessonBookOptional.isPresent()) {
            return lessonBookOptional.get();
        }
        else {
            return createLessonBook(language, difficulty, user);
        }
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
                .title(String.format("%s for %s Learners", language, difficulty))
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

    @Transactional
    public boolean deleteLessonBook(Long lessonBookId, User user) {
        return lessonBookRepository.deleteLessonBookById(lessonBookId, user) > 0;
    }
}
