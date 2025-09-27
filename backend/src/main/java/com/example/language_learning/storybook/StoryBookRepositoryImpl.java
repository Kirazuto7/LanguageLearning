package com.example.language_learning.storybook;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.language_learning.generated.jooq.tables.ShortStories.SHORT_STORIES;
import static com.example.language_learning.generated.jooq.tables.StoryBooks.STORY_BOOKS;
import static com.example.language_learning.generated.jooq.tables.StoryPages.STORY_PAGES;
import static com.example.language_learning.generated.jooq.tables.StoryParagraphs.STORY_PARAGRAPHS;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItems.STORY_VOCABULARY_ITEMS;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class StoryBookRepositoryImpl implements StoryBookRepositoryCustom {

    private final DSLContext dsl;

    @Override
    public Optional<StoryBook> findStoryBookDetailsById(Long id) {
        return dsl.select(
            STORY_BOOKS.ID,
            STORY_BOOKS.TITLE,
            STORY_BOOKS.DIFFICULTY,
            STORY_BOOKS.LANGUAGE,
            // Create a nested collection of short stories for each story book.
            multiset(
                dsl.select(
                    SHORT_STORIES.ID,
                    SHORT_STORIES.CHAPTER_NUMBER,
                    SHORT_STORIES.TITLE,
                    SHORT_STORIES.NATIVE_TITLE,
                    SHORT_STORIES.GENRE,
                    SHORT_STORIES.TOPIC,
                    // nested pages
                    multiset(
                        dsl.select(
                            STORY_PAGES.ID,
                            STORY_PAGES.PAGE_NUMBER,
                            STORY_PAGES.TYPE,
                            STORY_PAGES.IMAGE_URL,
                            STORY_PAGES.ENGLISH_SUMMARY,
                            multiset(
                                dsl.select(
                                    STORY_PARAGRAPHS.ID,
                                    STORY_PARAGRAPHS.PARAGRAPH_NUMBER,
                                    STORY_PARAGRAPHS.CONTENT
                                )
                                .from(STORY_PARAGRAPHS)
                                .where(STORY_PARAGRAPHS.STORY_PAGE_ID.eq(STORY_PAGES.ID))
                            ).as("paragraphs"),
                            multiset(
                                dsl.select(
                                    STORY_VOCABULARY_ITEMS.ID,
                                    STORY_VOCABULARY_ITEMS.WORD,
                                    STORY_VOCABULARY_ITEMS.TRANSLATION,
                                    STORY_VOCABULARY_ITEMS.PAGE_NUMBER
                                )
                                .from(STORY_VOCABULARY_ITEMS)
                                .where(STORY_VOCABULARY_ITEMS.STORY_PAGE_ID.eq(STORY_PAGES.ID))
                            ).as("vocabulary")
                        )
                        .from(STORY_PAGES)
                        .where(STORY_PAGES.SHORT_STORY_ID.eq(SHORT_STORIES.ID))
                    ).as("storyPages")
                )
                .from(SHORT_STORIES)
                .where(SHORT_STORIES.STORY_BOOK_ID.eq(STORY_BOOKS.ID))
            ).as("shortStories")
        )
        .from(STORY_BOOKS)
        .where(STORY_BOOKS.ID.eq(id))
        .fetchOptionalInto(StoryBook.class);
    }
}
