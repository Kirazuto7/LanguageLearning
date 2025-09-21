package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.StoryBook;
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
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public Optional<ShortStory> getShortStory(Long shortStoryId) {
        return shortStoryRepository.findById(shortStoryId);
    }

    @Transactional(readOnly = true)
    public ShortStoryDTO getShortStoryDtoByIdAndUser(Long shortStoryId, User user) {
        return shortStoryRepository.findByIdAndUserWithPages(shortStoryId, user)
                .map(dtoMapper::toDto)
                .orElse(null);
    }

    @Transactional
    public ShortStory createShortStory(StoryBook storyBook, int storyNumber, String title, String nativeTitle, String topic, String genre) {
        ShortStory shortStory = ShortStory.builder()
                .storyBook(storyBook)
                .chapterNumber(storyNumber)
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
}
