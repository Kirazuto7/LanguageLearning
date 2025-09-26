package com.example.language_learning.storybook;

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
        StoryBookDTO dto = dtoMapper.toDto(storyBook);
        log.info("Returning StoryBookDTO: {}", dto);
        return dto;
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
        Optional<StoryBook> storyBookOptional = storyBookRepository.findByUserAndLanguageAndDifficulty(user, language, difficulty);
        storyBookOptional.ifPresent(book -> {
            List<Long> storyIds = book.getShortStories().stream().map(ShortStory::getId).collect(Collectors.toList());

            if (!storyIds.isEmpty()) {
                // Fetch pages with paragraphs and group them by their parent story's ID
                Map<Long, List<StoryPage>> pagesWithParagraphsByStoryId = storyPageRepository.loadPagesWithParagraphsIn(storyIds)
                        .stream()
                        .collect(Collectors.groupingBy(page -> page.getShortStory().getId()));

                // Fetch pages with vocabulary and group them by their parent story's ID
                Map<Long, List<StoryPage>> pagesWithVocabularyByStoryId = storyPageRepository.loadPagesWithVocabularyIn(storyIds)
                        .stream()
                        .collect(Collectors.groupingBy(page -> page.getShortStory().getId()));

                // Set the fully loaded pages back onto the story objects
                book.getShortStories().forEach(story -> story.setStoryPages(pagesWithParagraphsByStoryId.get(story.getId())));
                book.getShortStories().forEach(story -> story.setStoryPages(pagesWithVocabularyByStoryId.get(story.getId())));
            }
        });
        return storyBookOptional;
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
