package com.example.language_learning.storybook;

import com.example.language_learning.shared.exceptions.ResourceNotFoundException;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.requests.StoryBookRequest;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryRepository;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageRepository;
import com.example.language_learning.user.User;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryBookService {

    private final StoryBookRepository storyBookRepository;
    private final StoryPageRepository storyPageRepository;
    private final DtoMapper dtoMapper;

    private static final List<String> TITLE_FORMATS = List.of(
            "A Collection of %s Stories",
            "A Treasury of %s Tales",
            "The %s Storybook Anthology",
            "The Library of %s Adventures",
            "A Journey Through %s Stories",
            "The %s Explorer's Storybook",
            "Voyages in %s",
            "Realms of %s: A Story Collection",
            "Whispers of %s: Tales and Fables",
            "A Whole New World of %s Stories"
    );
    private final Random random = new Random();

    @Transactional
    public StoryBookDTO findOrCreateBookDTO(StoryBookRequest request, User user) {
        StoryBook storyBook = findOrCreateBook(request.language(), request.difficulty(), user);
        log.info("StoryBook Entity: {}", storyBook);
        StoryBookDTO dto = dtoMapper.toDto(storyBook);
        log.info("Returning StoryBookDTO: {}", dto);
        return dto;
    }

    @Transactional
    public StoryBook findOrCreateBook(String language, String difficulty, User user) {
        Optional<StoryBook> storyBookOptional = getStoryBook(language, difficulty, user);

        if (storyBookOptional.isPresent()) {
            // Story book exists, deep fetch
            StoryBook storyBook = storyBookOptional.get();
            return storyBookRepository.findStoryBookDetailsById(storyBook.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Storybook with id '" + storyBook.getId() + "' not found"));
        }
        else {
            // Story book does not exist, create initial shell
            return createStoryBook(language, difficulty, user);
        }
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
        String randomFormat = TITLE_FORMATS.get(random.nextInt(TITLE_FORMATS.size()));
        String newTitle = String.format(randomFormat, language);

        StoryBook newBook = StoryBook.builder()
                .title(newTitle)
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
