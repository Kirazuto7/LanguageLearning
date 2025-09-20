package com.example.language_learning.storybook;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.requests.StoryBookRequest;
import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoryBookService {
    private final StoryBookRepository storyBookRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public StoryBookDTO findOrCreateBookDTO(StoryBookRequest request, User user) {
        StoryBook storyBook = findOrCreateBook(request.language(), request.difficulty(), user);
        return dtoMapper.toDto(storyBook);
    }

    @Transactional
    public StoryBook findOrCreateBook(String language, String difficulty, User user) {
        return getStoryBook(language, difficulty, user)
                .orElseGet(() -> createStoryBook(language, difficulty, user));
    }

    @Transactional(readOnly = true)
    public List<StoryBookDTO> fetchUserStoryBooks(User user) {
        return storyBookRepository.findAllByUser(user).stream()
                .map(dtoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<StoryBook> getStoryBook(String language, String difficulty, User user) {
        return storyBookRepository.findByUserAndLanguageAndDifficulty(user, language, difficulty);
    }

    @Transactional
    public StoryBook createStoryBook(String language, String difficulty, User user) {
        StoryBook newBook = StoryBook.builder()
                .title(String.format("%s Storybook for %s Learners", language, difficulty))
                .language(language)
                .difficulty(difficulty)
                .user(user)
                .build();
        return storyBookRepository.save(newBook);
    }

    @Transactional
    public StoryBook save(StoryBook book) {
        return storyBookRepository.save(book);
    }
}
