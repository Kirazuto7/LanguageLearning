package com.example.language_learning.storybook;

import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.language_learning.generated.jooq.tables.ShortStory.SHORT_STORY;
import static com.example.language_learning.generated.jooq.tables.StoryBook.STORY_BOOK;
import static com.example.language_learning.generated.jooq.tables.StoryPage.STORY_PAGE;
import static com.example.language_learning.generated.jooq.tables.StoryParagraph.STORY_PARAGRAPH;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItem.STORY_VOCABULARY_ITEM;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class StoryBookRepositoryImpl implements StoryBookRepositoryCustom {

    private final DSLContext dsl;

    @Override
    public Optional<StoryBook> findStoryBookDetailsById(Long id) {
        return dsl.select(
            STORY_BOOK.ID,
            STORY_BOOK.TITLE,
            STORY_BOOK.DIFFICULTY,
            STORY_BOOK.LANGUAGE,
            // Create a nested collection of short stories for each story book.
            multiset(
                dsl.select(
                    SHORT_STORY.ID,
                    SHORT_STORY.CHAPTER_NUMBER,
                    SHORT_STORY.TITLE,
                    SHORT_STORY.NATIVE_TITLE,
                    SHORT_STORY.GENRE,
                    SHORT_STORY.TOPIC,
                    // nested pages
                    multiset(
                        dsl.select(
                            STORY_PAGE.ID,
                            STORY_PAGE.PAGE_NUMBER,
                            STORY_PAGE.TYPE,
                            STORY_PAGE.IMAGE_URL,
                            STORY_PAGE.ENGLISH_SUMMARY,
                            multiset(
                                dsl.select(
                                    STORY_PARAGRAPH.ID,
                                    STORY_PARAGRAPH.PARAGRAPH_NUMBER,
                                    STORY_PARAGRAPH.CONTENT
                                )
                                .from(STORY_PARAGRAPH)
                                .where(STORY_PARAGRAPH.STORY_PAGE_ID.eq(STORY_PAGE.ID))
                            ).as("paragraphs"),
                            multiset(
                                dsl.select(
                                    STORY_VOCABULARY_ITEM.ID,
                                    STORY_VOCABULARY_ITEM.WORD,
                                    STORY_VOCABULARY_ITEM.TRANSLATION,
                                    STORY_VOCABULARY_ITEM.PAGE_NUMBER
                                )
                                .from(STORY_VOCABULARY_ITEM)
                                .where(STORY_VOCABULARY_ITEM.STORY_PAGE_ID.eq(STORY_PAGE.ID))
                            ).as("vocabulary")
                        )
                        .from(STORY_PAGE)
                        .where(STORY_PAGE.SHORT_STORY_ID.eq(SHORT_STORY.ID))
                    ).as("storyPages")
                )
                .from(SHORT_STORY)
                .where(SHORT_STORY.STORY_BOOK_ID.eq(STORY_BOOK.ID))
            ).as("shortStories")
        )
        .from(STORY_BOOK)
        .where(STORY_BOOK.ID.eq(id))
        .fetchOptional(r -> {
            StoryBook storyBook = new StoryBook();
            storyBook.setId(r.get(STORY_BOOK.ID));
            storyBook.setTitle(r.get(STORY_BOOK.TITLE));
            storyBook.setDifficulty(r.get(STORY_BOOK.DIFFICULTY));
            storyBook.setLanguage(r.get(STORY_BOOK.LANGUAGE));
            List<ShortStory> shortStories = new ArrayList<>();
            Result<Record> shortStoryRecords = r.get("shortStories", Result.class);
            for (Record ssr : shortStoryRecords) {
                ShortStory shortStory = new ShortStory();
                shortStory.setId(ssr.get(SHORT_STORY.ID));
                shortStory.setChapterNumber(ssr.get(SHORT_STORY.CHAPTER_NUMBER));
                shortStory.setTitle(ssr.get(SHORT_STORY.TITLE));
                shortStory.setNativeTitle(ssr.get(SHORT_STORY.NATIVE_TITLE));
                shortStory.setGenre(ssr.get(SHORT_STORY.GENRE));
                shortStory.setTopic(ssr.get(SHORT_STORY.TOPIC));

                List<StoryPage> storyPages = new ArrayList<>();
                Result<Record> storyPageRecords = ssr.get("storyPages", Result.class);
                for (Record srp : storyPageRecords) {
                    StoryPage storyPage = new StoryPage();
                    storyPage.setId(srp.get(STORY_PAGE.ID));
                    storyPage.setPageNumber(srp.get(STORY_PAGE.PAGE_NUMBER));
                    storyPage.setType(StoryPageType.valueOf(srp.get(STORY_PAGE.TYPE, String.class)));
                    storyPage.setImageUrl(srp.get(STORY_PAGE.IMAGE_URL));
                    storyPage.setEnglishSummary(srp.get(STORY_PAGE.ENGLISH_SUMMARY));

                    List<StoryParagraph> paragraphs = new ArrayList<>();
                    Result<Record> paragraphRecords = srp.get("paragraphs", Result.class);
                    for (Record pr : paragraphRecords) {
                        StoryParagraph paragraph = new StoryParagraph();
                        paragraph.setId(pr.get(STORY_PARAGRAPH.ID));
                        paragraph.setParagraphNumber(pr.get(STORY_PARAGRAPH.PARAGRAPH_NUMBER));
                        paragraph.setContent(pr.get(STORY_PARAGRAPH.CONTENT));
                        paragraphs.add(paragraph);
                    }
                    storyPage.setParagraphs(paragraphs);

                    List<StoryVocabularyItem> vocabulary = new ArrayList<>();
                    Result<Record> vocabularyRecords = srp.get("vocabulary", Result.class);
                    for (Record vr : vocabularyRecords) {
                        StoryVocabularyItem item = new StoryVocabularyItem();
                        item.setId(vr.get(STORY_VOCABULARY_ITEM.ID));
                        item.setWord(vr.get(STORY_VOCABULARY_ITEM.WORD));
                        item.setTranslation(vr.get(STORY_VOCABULARY_ITEM.TRANSLATION));
                        item.setPageNumber(vr.get(STORY_VOCABULARY_ITEM.PAGE_NUMBER));
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
