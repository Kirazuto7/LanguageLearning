package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.ai.enums.Language;
import com.example.language_learning.shared.exceptions.LanguageException;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.shared.services.NlpService;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryRepository;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryPageService {
    private final StoryPageRepository storyPageRepository;
    private final ShortStoryRepository shortStoryRepository;
    private final DtoMapper dtoMapper;
    private final NlpService nlpService;

    @Transactional
    public StoryPage createAndPersistPage(ShortStory shortStory, StoryPageDTO storyPageDTO) {
        ShortStory managedShortStory = findByIdAndInitializeCollections(shortStory.getId());

        String languageValue = managedShortStory.getStoryBook().getLanguage();
        Language language = Language.fromString(languageValue);
        if (language == null) {
            throw new LanguageException("Invalid language value: " + languageValue);
        }

        StoryPage storyPage = new StoryPage();
        storyPage.setShortStory(managedShortStory);
        storyPage.setType(storyPageDTO.type());
        storyPage.setEnglishSummary(storyPageDTO.englishSummary());
        storyPage.setImageUrl(storyPageDTO.imageUrl());

        // 1. Process vocabulary first to get the stems for this page
        Set<String> vocabStemsForPage = new HashSet<>();
        if (storyPageDTO.vocabulary() != null) {
            storyPageDTO.vocabulary().forEach(vDto -> {
                StoryVocabularyItem vocabItem = dtoMapper.toEntity(vDto);
                String stem = nlpService.getLemma(vocabItem.getWord(), language);
                vocabItem.setStem(stem);
                storyPage.addVocabulary(vocabItem);
                vocabStemsForPage.add(stem);
            });
        }

        // 2. Process paragraphs and find words to highlight
        if (storyPageDTO.paragraphs() != null) {
            storyPageDTO.paragraphs().forEach(pDto -> {
                StoryParagraph paragraph = dtoMapper.toEntity(pDto);
                String content = paragraph.getContent();
                Set<String> wordsToHighlight = new HashSet<>();

                if (content != null && !content.isBlank() && !vocabStemsForPage.isEmpty()) {
                    String[] tokens = content.split("[\\s\\p{Punct}]+");
                    for (String token : tokens) {
                        if (token.isBlank()) continue;
                        String tokenLemma = nlpService.getLemma(token, language);
                        if (vocabStemsForPage.contains(tokenLemma)) {
                            wordsToHighlight.add(token);
                        }
                    }
                }
                paragraph.setWordsToHighlight(wordsToHighlight);
                storyPage.addParagraph(paragraph);
            });
        }
        return storyPageRepository.save(storyPage);
    }

    @Transactional
    public void batchCreateAndPersistPages(ShortStory shortStory, List<StoryPageDTO> storyPageDTOs) {
        ShortStory managedShortStory = findByIdAndInitializeCollections(shortStory.getId());

        String languageValue = managedShortStory.getStoryBook().getLanguage();
        Language language = Language.fromString(languageValue);
        if (language == null) {
            throw new LanguageException("Invalid language value: " + languageValue);
        }

        List<StoryPage> pagesToPersist = storyPageDTOs.stream().map(pageDto -> {
            StoryPage storyPage = new StoryPage();
            storyPage.setShortStory(managedShortStory);
            storyPage.setType(pageDto.type());
            storyPage.setEnglishSummary(pageDto.englishSummary());
            storyPage.setImageUrl(pageDto.imageUrl());

            // 1. Process vocabulary first to get the stems for this page
            Set<String> vocabStemsForPage = new HashSet<>();
            if (pageDto.vocabulary() != null) {
                pageDto.vocabulary().forEach(vDto -> {
                    StoryVocabularyItem vocabItem = dtoMapper.toEntity(vDto);
                    String stem = nlpService.getLemma(vocabItem.getWord(), language);
                    vocabItem.setStem(stem);
                    storyPage.addVocabulary(vocabItem);
                    vocabStemsForPage.add(stem);
                });
            }

            // 2. Process paragraphs and find words to highlight
            if (pageDto.paragraphs() != null) {
                pageDto.paragraphs().forEach(pDto -> {
                    StoryParagraph paragraph = dtoMapper.toEntity(pDto);
                    String content = paragraph.getContent();
                    Set<String> wordsToHighlight = new HashSet<>();

                    if (content != null && !content.isBlank() && !vocabStemsForPage.isEmpty()) {
                        String[] tokens = content.split("[\\s\\p{Punct}]+");
                        for (String token : tokens) {
                            if (token.isBlank()) continue;
                            String tokenLemma = nlpService.getLemma(token, language);
                            if (vocabStemsForPage.contains(tokenLemma)) {
                                wordsToHighlight.add(token);
                            }
                        }
                    }
                    paragraph.setWordsToHighlight(wordsToHighlight);
                    storyPage.addParagraph(paragraph);
                });
            }

            return storyPage;
        }).collect(Collectors.toList());

        storyPageRepository.batchInsertPages(managedShortStory, pagesToPersist);
    }

    private ShortStory findByIdAndInitializeCollections(Long storyId) {
        ShortStory shortStory = shortStoryRepository.findByIdWithPagesOnly(storyId)
                .orElseThrow(() -> new RuntimeException("ShortStory not found during page creation: " + storyId));
        storyPageRepository.loadPagesWithParagraphs(storyId);
        storyPageRepository.loadPagesWithVocabulary(storyId);
        return shortStory;
    }
}
