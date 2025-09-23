package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryPageService {
    private final StoryPageRepository storyPageRepository;
    private final ShortStoryRepository shortStoryRepository;
    private final DtoMapper dtoMapper;

    public int getLastPageForBook(Long id) {
        return storyPageRepository.findMaxPageNumberByBookId(id).orElse(0);
    }

    @Transactional
    public StoryPage createAndPersistPage(ShortStory shortStory, StoryPageDTO storyPageDTO, int pageNumber) {
        ShortStory managedShortStory = shortStoryRepository.findByIdWithPages(shortStory.getId())
                .orElseThrow(() -> new RuntimeException("ShortStory not found during page creation: " + shortStory.getId()));
        StoryPage storyPage = StoryPage.builder()
                .shortStory(managedShortStory)
                .pageNumber(pageNumber)
                .englishSummary(storyPageDTO.englishSummary())
                .paragraphs(storyPageDTO.paragraphs().stream()
                        .map(dtoMapper::toEntity)
                        .toList())
                .vocabulary(storyPageDTO.vocabulary().stream()
                        .map(dtoMapper::toEntity)
                        .toList())
                .build();
        return storyPageRepository.save(storyPage);
    }
}
