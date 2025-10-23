package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.shortstory.page.StoryPageRepository;
import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortStoryService {
    private final ShortStoryRepository shortStoryRepository;
    private final StoryPageRepository storyPageRepository;
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public Optional<ShortStory> getShortStory(Long shortStoryId) {
        return shortStoryRepository.findById(shortStoryId);
    }

    @Transactional(readOnly = true)
    public ShortStoryDTO getShortStoryDtoByIdAndUser(Long shortStoryId, User user) {
        return findByIdAndUserAndInitializeCollections(shortStoryId, user)
                .map(dtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Short Story not found or user does not have access."));
    }

    @Transactional
    public ShortStory createShortStory(StoryBook storyBook, String title, String nativeTitle, String topic, String genre) {
        ShortStory shortStory = ShortStory.builder()
                .storyBook(storyBook)
                .title(title)
                .nativeTitle(nativeTitle)
                .topic(topic)
                .genre(genre)
                .build();
        storyBook.addShortStory(shortStory);
        return shortStoryRepository.save(shortStory);
    }

    @Transactional
    public ShortStory saveShortStory(ShortStory shortStory) {
        return shortStoryRepository.save(shortStory);
    }

    private Optional<ShortStory> findByIdAndUserAndInitializeCollections(Long storyId, User user) {
        Optional<ShortStory> shortStoryOptional = shortStoryRepository.findByIdAndUserWithPagesOnly(storyId, user);
        shortStoryOptional.ifPresent(shortStory -> {
            storyPageRepository.loadPagesWithVocabulary(storyId);
            storyPageRepository.loadPagesWithParagraphs(storyId);
        });
        return shortStoryOptional;
    }

    @Transactional
    public void deleteShortStory(Long storyId) {
        shortStoryRepository.deleteStoryById(storyId);
    }
}
