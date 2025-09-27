package com.example.language_learning.storybook;

import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.example.language_learning.generated.jooq.tables.ShortStories.SHORT_STORIES;
import static com.example.language_learning.generated.jooq.tables.StoryBooks.STORY_BOOKS;
import static com.example.language_learning.generated.jooq.tables.StoryPages.STORY_PAGES;
import static com.example.language_learning.generated.jooq.tables.StoryParagraphs.STORY_PARAGRAPHS;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItems.STORY_VOCABULARY_ITEMS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor
public class StoryBookRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jooq.sql-dialect", () -> "POSTGRES");
    }

    private final StoryBookRepository storyBookRepository;
    private final DSLContext dsl;

    @Test
    void findStoryBookDetailsByID_shouldReturnCorrectStoryBook() {
        Long storyBookId = dsl.insertInto(STORY_BOOKS)
                .set(STORY_BOOKS.TITLE, "My First Story Book")
                .set(STORY_BOOKS.LANGUAGE, "ja")
                .set(STORY_BOOKS.DIFFICULTY, "A1")
                .returning(STORY_BOOKS.ID)
                .fetchOne()
                .getId();

        // Insert a related ShortStory
        Long shortStoryId = dsl.insertInto(SHORT_STORIES)
                .set(SHORT_STORIES.STORY_BOOK_ID, storyBookId)
                .set(SHORT_STORIES.CHAPTER_NUMBER, 1)
                .set(SHORT_STORIES.TITLE, "The Adventure Begins")
                .returning(SHORT_STORIES.ID)
                .fetchOne()
                .getId();

        // Insert a related StoryPage
        Long storyPageId = dsl.insertInto(STORY_PAGES)
                .set(STORY_PAGES.SHORT_STORY_ID, shortStoryId)
                .set(STORY_PAGES.PAGE_NUMBER, 1)
                .set(STORY_PAGES.TYPE, "story")
                .returning(STORY_PAGES.ID)
                .fetchOne()
                .getId();

        dsl.insertInto(STORY_PARAGRAPHS)
                .set(STORY_PARAGRAPHS.STORY_PAGE_ID, storyPageId)
                .set(STORY_PARAGRAPHS.PARAGRAPH_NUMBER, 1)
                .set(STORY_PARAGRAPHS.CONTENT, "Once upon a time...")
                .execute();

        // Insert a related VocabularyItem
        dsl.insertInto(STORY_VOCABULARY_ITEMS)
                .set(STORY_VOCABULARY_ITEMS.STORY_PAGE_ID, storyPageId)
                .set(STORY_VOCABULARY_ITEMS.PAGE_NUMBER, 1)
                .set(STORY_VOCABULARY_ITEMS.WORD, "昔々")
                .set(STORY_VOCABULARY_ITEMS.TRANSLATION, "Once upon a time")
                .execute();

        // 2. Act: Call the repository method
        Optional<StoryBook> result = storyBookRepository.findStoryBookDetailsById(storyBookId);

        // 3. Assert: Verify the results
        assertThat(result).isPresent();
        StoryBook foundBook = result.get();

        assertThat(foundBook.getTitle()).isEqualTo("My First Story Book");
        assertThat(foundBook.getShortStories()).hasSize(1);

        ShortStory foundShortStory = foundBook.getShortStories().get(0);
        assertThat(foundShortStory.getTitle()).isEqualTo("The Adventure Begins");
        assertThat(foundShortStory.getStoryPages()).hasSize(1);

        StoryPage foundPage = foundShortStory.getStoryPages().get(0);
        assertThat(foundPage.getPageNumber()).isEqualTo(1);
        assertThat(foundPage.getParagraphs()).hasSize(1);
        assertThat(foundPage.getVocabulary()).hasSize(1);
        assertThat(foundPage.getParagraphs().get(0).getContent()).isEqualTo("Once upon a time...");
        assertThat(foundPage.getVocabulary().get(0).getWord()).isEqualTo("昔々");

    }

}
