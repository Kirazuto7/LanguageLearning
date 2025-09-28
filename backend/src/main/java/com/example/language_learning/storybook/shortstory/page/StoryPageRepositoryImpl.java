package com.example.language_learning.storybook.shortstory.page;


import com.example.language_learning.generated.jooq.tables.records.StoryPageRecord;
import com.example.language_learning.generated.jooq.tables.records.StoryParagraphRecord;
import com.example.language_learning.generated.jooq.tables.records.StoryVocabularyItemRecord;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep5;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.language_learning.generated.jooq.tables.StoryPage.STORY_PAGE;
import static com.example.language_learning.generated.jooq.tables.StoryParagraph.STORY_PARAGRAPH;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItem.STORY_VOCABULARY_ITEM;

@Repository
@RequiredArgsConstructor
public class StoryPageRepositoryImpl implements StoryPageRepositoryCustom {

    private final DSLContext dsl;

    @Override
    @Transactional
    public void batchInsertPages(ShortStory shortStory, List<StoryPage> storyPages) {

        if (storyPages == null || storyPages.isEmpty()) {
            return;
        }

        // 1. Build a single INSERT statement with multiple VALUES clauses
        InsertValuesStep5<StoryPageRecord, Long, Integer, String, String, String> insertStep = dsl.insertInto(STORY_PAGE,
            STORY_PAGE.SHORT_STORY_ID,
            STORY_PAGE.PAGE_NUMBER,
            STORY_PAGE.TYPE,
            STORY_PAGE.IMAGE_URL,
            STORY_PAGE.ENGLISH_SUMMARY
        );

        for (StoryPage page : storyPages) {
            insertStep.values(
                shortStory.getId(),
                page.getPageNumber(),
                page.getType().name(),
                page.getImageUrl(),
                page.getEnglishSummary()
            );
        }

        // 2. Execute the insert and fetch the generated IDs
        Result<StoryPageRecord> insertedPages = insertStep.returning(STORY_PAGE.ID).fetch();

        // 3. Prepare records for paragraphs and vocabulary
        List<StoryParagraphRecord> paragraphRecords = new ArrayList<>();
        List<StoryVocabularyItemRecord> vocabularyItemRecords = new ArrayList<>();

        IntStream.range(0, storyPages.size()).forEach(i -> {
            StoryPage page = storyPages.get(i);
            Long pageId = insertedPages.get(i).getId();

            if (page.getParagraphs() != null) {
                for (StoryParagraph paragraph : page.getParagraphs()) {
                    StoryParagraphRecord record = dsl.newRecord(STORY_PARAGRAPH);
                    record.setStoryPageId(pageId);
                    record.setParagraphNumber(paragraph.getParagraphNumber());
                    record.setContent(paragraph.getContent());
                    paragraphRecords.add(record);
                }
            }

            if (page.getVocabulary() != null) {
                for (StoryVocabularyItem vocabItem : page.getVocabulary()) {
                    StoryVocabularyItemRecord record = dsl.newRecord(STORY_VOCABULARY_ITEM);
                    record.setStoryPageId(pageId);
                    record.setWord(vocabItem.getWord());
                    record.setTranslation(vocabItem.getTranslation());
                    record.setPageNumber(vocabItem.getPageNumber());
                    vocabularyItemRecords.add(record);
                }
            }
        });

        // 4. Batch insert paragraphs and vocabulary
        if (!paragraphRecords.isEmpty()) {
            dsl.batchInsert(paragraphRecords).execute();
        }

        if (!vocabularyItemRecords.isEmpty()) {
            dsl.batchInsert(vocabularyItemRecords).execute();
        }
    }
}
