package com.example.language_learning.storybook;

import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.language_learning.generated.jooq.tables.ShortStories.SHORT_STORIES;
import static com.example.language_learning.generated.jooq.tables.StoryBooks.STORY_BOOKS;
import static com.example.language_learning.generated.jooq.tables.StoryPages.STORY_PAGES;
import static com.example.language_learning.generated.jooq.tables.StoryParagraphs.STORY_PARAGRAPHS;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItems.STORY_VOCABULARY_ITEMS;
import static org.jooq.impl.DSL.cast;
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
                                    cast(STORY_PARAGRAPHS.CONTENT, String.class).as("content")
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
        .fetchOptional(r -> {
            StoryBook storyBook = new StoryBook();
            storyBook.setId(r.get(STORY_BOOKS.ID));
            storyBook.setTitle(r.get(STORY_BOOKS.TITLE));
            storyBook.setDifficulty(r.get(STORY_BOOKS.DIFFICULTY));
            storyBook.setLanguage(r.get(STORY_BOOKS.LANGUAGE));
            List<ShortStory> shortStories = new ArrayList<>();
            Result<org.jooq.Record> shortStoryRecords = r.get("shortStories", Result.class);
            for (org.jooq.Record ssr : shortStoryRecords) {
                ShortStory shortStory = new ShortStory();
                shortStory.setId(ssr.get(SHORT_STORIES.ID));
                shortStory.setChapterNumber(ssr.get(SHORT_STORIES.CHAPTER_NUMBER));
                shortStory.setTitle(ssr.get(SHORT_STORIES.TITLE));
                shortStory.setNativeTitle(ssr.get(SHORT_STORIES.NATIVE_TITLE));
                shortStory.setGenre(ssr.get(SHORT_STORIES.GENRE));
                shortStory.setTopic(ssr.get(SHORT_STORIES.TOPIC));

                List<StoryPage> storyPages = new ArrayList<>();
                Result<org.jooq.Record> storyPageRecords = ssr.get("storyPages", Result.class);
                for (org.jooq.Record srp : storyPageRecords) {
                    StoryPage storyPage = new StoryPage();
                    storyPage.setId(srp.get(STORY_PAGES.ID));
                    storyPage.setPageNumber(srp.get(STORY_PAGES.PAGE_NUMBER));
                    storyPage.setType(StoryPageType.valueOf(srp.get(STORY_PAGES.TYPE, String.class)));
                    storyPage.setImageUrl(srp.get(STORY_PAGES.IMAGE_URL));
                    storyPage.setEnglishSummary(srp.get(STORY_PAGES.ENGLISH_SUMMARY));

                    List<StoryParagraph> paragraphs = new ArrayList<>();
                    Result<org.jooq.Record> paragraphRecords = srp.get("paragraphs", Result.class);
                    for (org.jooq.Record pr : paragraphRecords) {
                        StoryParagraph paragraph = new StoryParagraph();
                        paragraph.setId(pr.get(STORY_PARAGRAPHS.ID));
                        paragraph.setParagraphNumber(pr.get(STORY_PARAGRAPHS.PARAGRAPH_NUMBER));
                        paragraph.setContent(pr.get(STORY_PARAGRAPHS.CONTENT));
                        paragraphs.add(paragraph);
                    }
                    storyPage.setParagraphs(paragraphs);

                    List<StoryVocabularyItem> vocabulary = new ArrayList<>();
                    Result<org.jooq.Record> vocabularyRecords = srp.get("vocabulary", Result.class);
                    for (org.jooq.Record vr : vocabularyRecords) {
                        StoryVocabularyItem item = new StoryVocabularyItem();
                        item.setId(vr.get(STORY_VOCABULARY_ITEMS.ID));
                        item.setWord(vr.get(STORY_VOCABULARY_ITEMS.WORD));
                        item.setTranslation(vr.get(STORY_VOCABULARY_ITEMS.TRANSLATION));
                        item.setPageNumber(vr.get(STORY_VOCABULARY_ITEMS.PAGE_NUMBER));
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
}
