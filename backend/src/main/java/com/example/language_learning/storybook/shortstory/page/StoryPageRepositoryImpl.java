package com.example.language_learning.storybook.shortstory.page;


import com.example.language_learning.generated.jooq.tables.records.StoryPageRecord;
import com.example.language_learning.generated.jooq.tables.records.StoryParagraphRecord;
import com.example.language_learning.generated.jooq.tables.records.StoryVocabularyItemRecord;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.language_learning.generated.jooq.tables.StoryPage.STORY_PAGE;
import static com.example.language_learning.generated.jooq.tables.StoryParagraph.STORY_PARAGRAPH;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItem.STORY_VOCABULARY_ITEM;

@Repository
@Slf4j
@RequiredArgsConstructor
public class StoryPageRepositoryImpl implements StoryPageRepositoryCustom {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void batchInsertPages(ShortStory shortStory, List<StoryPage> storyPages) {

        if (storyPages == null || storyPages.isEmpty()) {
            return;
        }

        // 1. Batch insert pages
        InsertValuesStep5<StoryPageRecord, Long, String, String, String, LocalDateTime> template =
                dsl.insertInto(
                        STORY_PAGE,
                        STORY_PAGE.SHORT_STORY_ID,
                        STORY_PAGE.TYPE,
                        STORY_PAGE.IMAGE_URL,
                        STORY_PAGE.ENGLISH_SUMMARY,
                        STORY_PAGE.CREATED_AT
                ).values((Long)null, (String)null, (String)null, (String)null, null);

        BatchBindStep batch = dsl.batch(template);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        // 2. Bind values for each page and then execute the batch
        for (StoryPage page : storyPages) {
            batch.bind(
                shortStory.getId(),
                page.getType().name(),
                page.getImageUrl(),
                page.getEnglishSummary(),
                now
            );
        }

        batch.execute();

        // 3. Fetch the generated IDs
        List<Long> insertedPageIds = dsl.select(STORY_PAGE.ID)
                .from(STORY_PAGE)
                .where(STORY_PAGE.SHORT_STORY_ID.eq(shortStory.getId()))
                .orderBy(STORY_PAGE.ID.asc())
                .limit(storyPages.size())
                .fetchInto(Long.class);

        // 4. Prepare records for paragraphs and vocabulary
        List<StoryParagraphRecord> paragraphRecords = new ArrayList<>();
        List<StoryVocabularyItemRecord> vocabularyItemRecords = new ArrayList<>();

        IntStream.range(0, storyPages.size()).forEach(i -> {
            StoryPage page = storyPages.get(i);
            Long pageId = insertedPageIds.get(i);

            if (page.getParagraphs() != null) {
                for (StoryParagraph paragraph : page.getParagraphs()) {
                    StoryParagraphRecord record = dsl.newRecord(STORY_PARAGRAPH);
                    record.setStoryPageId(pageId);
                    record.setParagraphNumber(paragraph.getParagraphNumber());
                    record.setContent(paragraph.getContent());
                    record.setCreatedAt(now);

                    // Convert the Set<String> to a JSONB object for JOOQ
                    try {
                        String jsonString = objectMapper.writeValueAsString(paragraph.getWordsToHighlight());
                        record.setWordsToHighlight(JSONB.valueOf(jsonString));
                    }
                    catch (JsonProcessingException e) {
                        log.error("Error serializing wordsToHighlight for paragraph {}", paragraph.getId(), e);
                        record.setWordsToHighlight(JSONB.valueOf("[]"));
                    }

                    paragraphRecords.add(record);
                }
            }

            if (page.getVocabulary() != null) {
                for (StoryVocabularyItem vocabItem : page.getVocabulary()) {
                    StoryVocabularyItemRecord record = dsl.newRecord(STORY_VOCABULARY_ITEM);
                    record.setStoryPageId(pageId);
                    record.setWord(vocabItem.getWord());
                    record.setStem(vocabItem.getStem());
                    record.setTranslation(vocabItem.getTranslation());
                    record.setCreatedAt(now);
                    vocabularyItemRecords.add(record);
                }
            }
        });

        // 5. Batch insert paragraphs and vocabulary
        if (!paragraphRecords.isEmpty()) {
            dsl.batchInsert(paragraphRecords).execute();
        }

        if (!vocabularyItemRecords.isEmpty()) {
            dsl.batchInsert(vocabularyItemRecords).execute();
        }
    }
}
