package com.example.language_learning.storybook;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.shared.services.ImageService;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import org.jooq.DSLContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.example.language_learning.generated.jooq.tables.ShortStory.SHORT_STORY;
import static com.example.language_learning.generated.jooq.tables.StoryBook.STORY_BOOK;
import static com.example.language_learning.generated.jooq.tables.StoryPage.STORY_PAGE;
import static com.example.language_learning.generated.jooq.tables.StoryParagraph.STORY_PARAGRAPH;
import static com.example.language_learning.generated.jooq.tables.StoryVocabularyItem.STORY_VOCABULARY_ITEM;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Slf4j
public class StoryBookRepositoryIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ImageService imageService() {
            return Mockito.mock(ImageService.class);
        }

        @Bean
        @Primary
        public AIEngine aiEngine() {
            return Mockito.mock(AIEngine.class);
        }
    }

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private StoryBookRepository storyBookRepository;
    @Autowired
    private DSLContext dsl;

    @Test
    void findStoryBookDetailsByID_shouldReturnCorrectStoryBook() {
        // 1. Arrange
        Long storyBookId = dsl.insertInto(STORY_BOOK)
                .set(STORY_BOOK.TITLE, "My First Story Book")
                .set(STORY_BOOK.LANGUAGE, "ja")
                .set(STORY_BOOK.DIFFICULTY, "A1")
                .returning(STORY_BOOK.ID)
                .fetchOne()
                .getId();

        Long shortStoryId = dsl.insertInto(SHORT_STORY)
                .set(SHORT_STORY.STORY_BOOK_ID, storyBookId)
                .set(SHORT_STORY.TITLE, "The Adventure Begins")
                .set(SHORT_STORY.NATIVE_TITLE, "冒険の始まり")
                .set(SHORT_STORY.GENRE, "Fantasy")
                .set(SHORT_STORY.TOPIC, "A magical journey")
                .returning(SHORT_STORY.ID)
                .fetchOne()
                .getId();

        Long storyPageId = dsl.insertInto(STORY_PAGE)
                .set(STORY_PAGE.SHORT_STORY_ID, shortStoryId)
                .set(STORY_PAGE.TYPE, StoryPageType.CONTENT.name())
                .set(STORY_PAGE.IMAGE_URL, "http://example.com/image.png")
                .set(STORY_PAGE.ENGLISH_SUMMARY, "A summary of the page.")
                .returning(STORY_PAGE.ID)
                .fetchOne()
                .getId();

        dsl.insertInto(STORY_PARAGRAPH)
                .set(STORY_PARAGRAPH.STORY_PAGE_ID, storyPageId)
                .set(STORY_PARAGRAPH.PARAGRAPH_NUMBER, 1)
                .set(STORY_PARAGRAPH.CONTENT, "Once upon a time...")
                .execute();

        dsl.insertInto(STORY_VOCABULARY_ITEM)
                .set(STORY_VOCABULARY_ITEM.STORY_PAGE_ID, storyPageId)
                .set(STORY_VOCABULARY_ITEM.WORD, "昔々")
                .set(STORY_VOCABULARY_ITEM.TRANSLATION, "Once upon a time")
                .execute();

        // 2. Act
        Optional<StoryBook> result = storyBookRepository.findStoryBookDetailsById(storyBookId);

        // 3. Assert
        assertThat(result).isPresent();
        StoryBook foundBook = result.get();

        // Assert top-level StoryBook fields
        assertThat(foundBook.getTitle()).isEqualTo("My First Story Book");
        assertThat(foundBook.getLanguage()).isEqualTo("ja");
        assertThat(foundBook.getDifficulty()).isEqualTo("A1");
        assertThat(foundBook.getShortStories()).isNotNull().hasSize(1);

        // Assert nested ShortStory fields
        ShortStory foundShortStory = foundBook.getShortStories().get(0);
        assertThat(foundShortStory.getTitle()).isEqualTo("The Adventure Begins");
        assertThat(foundShortStory.getNativeTitle()).isEqualTo("冒険の始まり");
        assertThat(foundShortStory.getGenre()).isEqualTo("Fantasy");
        assertThat(foundShortStory.getTopic()).isEqualTo("A magical journey");
        assertThat(foundShortStory.getStoryPages()).isNotNull().hasSize(1);

        // Assert nested StoryPage fields
        StoryPage foundPage = foundShortStory.getStoryPages().get(0);
        assertThat(foundPage.getType()).isEqualTo(StoryPageType.CONTENT);
        assertThat(foundPage.getImageUrl()).isEqualTo("http://example.com/image.png");
        assertThat(foundPage.getEnglishSummary()).isEqualTo("A summary of the page.");

        // Assert that the nested collections are not null before checking their contents
        assertThat(foundPage.getParagraphs()).isNotNull().hasSize(1);
        assertThat(foundPage.getVocabulary()).isNotNull().hasSize(1);

        // Assert deeply nested Paragraph and Vocabulary fields
        assertThat(foundPage.getParagraphs().get(0).getContent()).isEqualTo("Once upon a time...");
        assertThat(foundPage.getVocabulary().get(0).getWord()).isEqualTo("昔々");

        // Final logging for confirmation
        result.ifPresent(book -> log.info("Final fetched StoryBook object:\n{}", book));
    }

}
