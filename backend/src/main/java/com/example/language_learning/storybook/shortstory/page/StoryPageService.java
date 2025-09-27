package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryRepository;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
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
        ShortStory managedShortStory = findByIdAndInitializeCollections(shortStory.getId());

        return switch (storyPageDTO.type()) {
            case CONTENT -> {
                StoryPage storyPage = new StoryPage();
                storyPage.setShortStory(managedShortStory);
                storyPage.setType(StoryPageType.CONTENT);
                storyPage.setPageNumber(pageNumber);
                storyPage.setEnglishSummary(storyPageDTO.englishSummary());
                storyPage.setImageUrl(storyPageDTO.imageUrl());

                storyPageDTO.paragraphs().forEach(pDto -> {
                    StoryParagraph paragraph = dtoMapper.toEntity(pDto);
                    storyPage.addParagraph(paragraph);
                });

                storyPageDTO.vocabulary().forEach(vDto -> {
                    StoryVocabularyItem vocabItem = dtoMapper.toEntity(vDto);
                    storyPage.addVocabulary(vocabItem);
                });

                yield storyPageRepository.save(storyPage);
            }
            case VOCABULARY -> {
                StoryPage storyPage = new StoryPage();
                storyPage.setShortStory(managedShortStory);
                storyPage.setType(StoryPageType.VOCABULARY);
                storyPage.setPageNumber(pageNumber);
                storyPage.setEnglishSummary(storyPageDTO.englishSummary());
                storyPage.setImageUrl(storyPageDTO.imageUrl());

                storyPageDTO.vocabulary().forEach(vDto -> {
                    StoryVocabularyItem vocabItem = dtoMapper.toEntity(vDto);
                    storyPage.addVocabulary(vocabItem);
                });

                yield storyPageRepository.save(storyPage);
            }
        };
    }

    private ShortStory findByIdAndInitializeCollections(Long storyId) {
        ShortStory shortStory = shortStoryRepository.findByIdWithPagesOnly(storyId)
                .orElseThrow(() -> new RuntimeException("ShortStory not found during page creation: " + storyId));
        storyPageRepository.loadPagesWithParagraphs(storyId);
        storyPageRepository.loadPagesWithVocabulary(storyId);
        return shortStory;
    }
}
