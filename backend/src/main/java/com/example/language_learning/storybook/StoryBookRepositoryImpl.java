package com.example.language_learning.storybook;

import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import com.example.language_learning.user.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static com.example.language_learning.generated.jooq.tables.ShortStory.SHORT_STORY;
import static com.example.language_learning.generated.jooq.tables.StoryBook.STORY_BOOK;
import static com.example.language_learning.generated.jooq.tables.StoryPage.STORY_PAGE;
import static com.example.language_learning.generated.jooq.tables.StoryParagraph.STORY_PARAGRAPH;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItem.STORY_VOCABULARY_ITEM;
import static org.jooq.impl.DSL.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class StoryBookRepositoryImpl implements StoryBookRepositoryCustom {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<StoryBook> findStoryBookDetailsById(Long id, User user) {
        return dsl.select(
            STORY_BOOK.ID,
            STORY_BOOK.TITLE,
            STORY_BOOK.DIFFICULTY,
            STORY_BOOK.LANGUAGE,
            STORY_BOOK.CREATED_AT,
            // Create a nested collection of short stories for each story book.
            multiset(
                dsl.select(
                    SHORT_STORY.ID,
                    SHORT_STORY.TITLE,
                    SHORT_STORY.NATIVE_TITLE,
                    SHORT_STORY.GENRE,
                    SHORT_STORY.TOPIC,
                    SHORT_STORY.CREATED_AT,
                    // nested pages
                    multiset(
                        dsl.select(
                            STORY_PAGE.ID,
                            STORY_PAGE.TYPE,
                            STORY_PAGE.IMAGE_URL,
                            STORY_PAGE.ENGLISH_SUMMARY,
                            STORY_PAGE.CREATED_AT,
                            multiset(
                                dsl.select(
                                    STORY_PARAGRAPH.ID,
                                    STORY_PARAGRAPH.CONTENT,
                                    STORY_PARAGRAPH.WORDS_TO_HIGHLIGHT,
                                    STORY_PARAGRAPH.CREATED_AT
                                )
                                .from(STORY_PARAGRAPH)
                                .where(STORY_PARAGRAPH.STORY_PAGE_ID.eq(STORY_PAGE.ID))
                            ).as("paragraphs"),
                            multiset(
                                dsl.selectDistinct(
                                    STORY_VOCABULARY_ITEM.ID,
                                    STORY_VOCABULARY_ITEM.WORD,
                                    STORY_VOCABULARY_ITEM.STEM,
                                    STORY_VOCABULARY_ITEM.TRANSLATION,
                                    STORY_VOCABULARY_ITEM.CREATED_AT
                                )
                                .from(STORY_VOCABULARY_ITEM)
                                .where(STORY_VOCABULARY_ITEM.STORY_PAGE_ID.eq(STORY_PAGE.ID))
                            ).as("vocabulary")
                        )
                        .from(STORY_PAGE)
                        .where(STORY_PAGE.SHORT_STORY_ID.eq(SHORT_STORY.ID))
                        .orderBy(STORY_PAGE.ID.asc())
                    ).as("storyPages")
                )
                .from(SHORT_STORY)
                .where(SHORT_STORY.STORY_BOOK_ID.eq(STORY_BOOK.ID))
                .orderBy(SHORT_STORY.ID.asc())
            ).as("shortStories")
        )
        .from(STORY_BOOK)
        .where(STORY_BOOK.ID.eq(id))
        .and(STORY_BOOK.USER_ID.eq(user.getId()))
        .fetchOptional(r -> {
            StoryBook storyBook = new StoryBook();
            storyBook.setId(r.get(STORY_BOOK.ID));
            storyBook.setTitle(r.get(STORY_BOOK.TITLE));
            storyBook.setDifficulty(r.get(STORY_BOOK.DIFFICULTY));
            storyBook.setLanguage(r.get(STORY_BOOK.LANGUAGE));
            storyBook.setCreatedAt(r.get(STORY_BOOK.CREATED_AT));
            List<ShortStory> shortStories = new ArrayList<>();
            Result<Record> shortStoryRecords = r.get("shortStories", Result.class);
            for (Record ssr : shortStoryRecords) {
                ShortStory shortStory = new ShortStory();
                shortStory.setId(ssr.get(SHORT_STORY.ID));
                shortStory.setTitle(ssr.get(SHORT_STORY.TITLE));
                shortStory.setNativeTitle(ssr.get(SHORT_STORY.NATIVE_TITLE));
                shortStory.setGenre(ssr.get(SHORT_STORY.GENRE));
                shortStory.setTopic(ssr.get(SHORT_STORY.TOPIC));
                shortStory.setCreatedAt(ssr.get(SHORT_STORY.CREATED_AT));

                List<StoryPage> storyPages = new ArrayList<>();
                Result<Record> storyPageRecords = ssr.get("storyPages", Result.class);
                for (Record srp : storyPageRecords) {
                    StoryPage storyPage = new StoryPage();
                    storyPage.setId(srp.get(STORY_PAGE.ID));
                    storyPage.setType(StoryPageType.valueOf(srp.get(STORY_PAGE.TYPE, String.class)));
                    storyPage.setImageUrl(srp.get(STORY_PAGE.IMAGE_URL));
                    storyPage.setEnglishSummary(srp.get(STORY_PAGE.ENGLISH_SUMMARY));
                    storyPage.setCreatedAt(srp.get(STORY_PAGE.CREATED_AT));

                    List<StoryParagraph> paragraphs = new ArrayList<>();
                    Result<Record> paragraphRecords = srp.get("paragraphs", Result.class);
                    for (Record pr : paragraphRecords) {
                        StoryParagraph paragraph = new StoryParagraph();
                        paragraph.setId(pr.get(STORY_PARAGRAPH.ID));
                        paragraph.setContent(pr.get(STORY_PARAGRAPH.CONTENT));
                        paragraph.setCreatedAt(pr.get(STORY_PARAGRAPH.CREATED_AT));

                        JSONB wordsToHighlightJson = pr.get(STORY_PARAGRAPH.WORDS_TO_HIGHLIGHT);
                        if (wordsToHighlightJson != null && wordsToHighlightJson.data() != null) {
                            try {
                                Set<String> wordsToHighlight = objectMapper.readValue(wordsToHighlightJson.data(), new TypeReference<Set<String>>() {});
                                paragraph.setWordsToHighlight(wordsToHighlight);
                            }
                            catch (IOException e) {
                                log.error("Error deserializing wordsToHighlight for paragraph {}", paragraph.getId(), e);
                                paragraph.setWordsToHighlight(new HashSet<>());
                            }
                        }
                        else {
                            paragraph.setWordsToHighlight(new HashSet<>());
                        }

                        paragraphs.add(paragraph);
                    }
                    storyPage.setParagraphs(paragraphs);

                    List<StoryVocabularyItem> vocabulary = new ArrayList<>();
                    Result<Record> vocabularyRecords = srp.get("vocabulary", Result.class);
                    for (Record vr : vocabularyRecords) {
                        StoryVocabularyItem item = new StoryVocabularyItem();
                        item.setId(vr.get(STORY_VOCABULARY_ITEM.ID));
                        item.setWord(vr.get(STORY_VOCABULARY_ITEM.WORD));
                        item.setStem(vr.get(STORY_VOCABULARY_ITEM.STEM));
                        item.setTranslation(vr.get(STORY_VOCABULARY_ITEM.TRANSLATION));
                        item.setCreatedAt(vr.get(STORY_VOCABULARY_ITEM.CREATED_AT));
                        vocabulary.add(item);
                    }
                    storyPage.setVocabulary(vocabulary);
                    storyPages.add(storyPage);
                }
                shortStory.setStoryPages(storyPages);
                shortStories.add(shortStory);
            }

            storyBook.setShortStories(shortStories);
            return storyBook;
        });
    }

    @Override
    @Transactional
    public int deleteStoryBookById(Long storyBookId, User user) {

        var stories = name("stories").as(
            select(SHORT_STORY.ID).from(SHORT_STORY).where(SHORT_STORY.STORY_BOOK_ID.eq(storyBookId))
        );

        var pages = name("pages").as(
            select(STORY_PAGE.ID).from(STORY_PAGE).where(STORY_PAGE.SHORT_STORY_ID.in(
                select(field("id", Long.class)).from(stories)
            ))
        );

        deleteGrandchildren(stories, pages);
        deleteChildren(storyBookId, stories, pages);

        int deletedRows = dsl.deleteFrom(STORY_BOOK)
            .where(STORY_BOOK.ID.eq(storyBookId))
            .and(STORY_BOOK.USER_ID.eq(user.getId()))
            .execute();
        log.info("Deleted story book with id: {}", storyBookId);
        return deletedRows;
    }

    private void deleteGrandchildren(CommonTableExpression<?> stories, CommonTableExpression<?> pages) {
        dsl.deleteFrom(STORY_PARAGRAPH)
                .where(STORY_PARAGRAPH.STORY_PAGE_ID.in(
                    dsl.with(stories).with(pages).select(
                        field("id", Long.class)
                    ).from(pages)
                )).execute();

        dsl.deleteFrom(STORY_VOCABULARY_ITEM)
                .where(STORY_VOCABULARY_ITEM.STORY_PAGE_ID.in(
                    dsl.with(stories).with(pages).select(
                        field("id", Long.class)
                    ).from(pages)
                )).execute();
    }

    private void deleteChildren(Long storyBookId, CommonTableExpression<?> stories, CommonTableExpression<?> pages) {
        dsl.deleteFrom(STORY_PAGE)
                .where(STORY_PAGE.SHORT_STORY_ID.in(
                    dsl.with(stories).select(
                        field("id", Long.class)
                    ).from(stories)
                )).execute();

        dsl.deleteFrom(SHORT_STORY).where(SHORT_STORY.STORY_BOOK_ID.eq(storyBookId)).execute();
    }
}
