package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.shared.services.ImageService;
import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import org.jooq.DSLContext;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Map;

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
public class StoryPageRepositoryIntegrationTest {

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
    private StoryPageRepository storyPageRepository;

    @Autowired
    private DSLContext dsl;

    private ShortStory managedShortStory;

    @BeforeEach
    void setUp() {
        Long storyBookId = dsl.insertInto(STORY_BOOK)
                .set(STORY_BOOK.TITLE, "Test Story Book")
                .set(STORY_BOOK.LANGUAGE, "en")
                .set(STORY_BOOK.DIFFICULTY, "B1")
                .returning(STORY_BOOK.ID)
                .fetchOne().getId();

        Long shortStoryId = dsl.insertInto(SHORT_STORY)
                .set(SHORT_STORY.STORY_BOOK_ID, storyBookId)
                .set(SHORT_STORY.CHAPTER_NUMBER, 1)
                .set(SHORT_STORY.TITLE, "The Missing Chapter")
                .set(SHORT_STORY.NATIVE_TITLE, "The Missing Chapter")
                .set(SHORT_STORY.GENRE, "Mystery")
                .set(SHORT_STORY.TOPIC, "A detective story")
                .returning(SHORT_STORY.ID)
                .fetchOne().getId();

        managedShortStory = new ShortStory();
        managedShortStory.setId(shortStoryId);
        StoryBook storyBook = new StoryBook();
        storyBook.setId(storyBookId);
        managedShortStory.setStoryBook(storyBook);
    }

    @Test
    void batchInsertPages_shouldPersistAllDataCorrectly() {
        // 1. Arrange
        StoryParagraph paragraph1 = new StoryParagraph();
        paragraph1.setParagraphNumber(1);
        paragraph1.setContent("This is the first paragraph.");

        StoryVocabularyItem vocabItem1 = new StoryVocabularyItem();
        vocabItem1.setWord("persist");
        vocabItem1.setTranslation("to continue to exist");
        vocabItem1.setPageNumber(1);

        StoryPage contentPage = new StoryPage();
        contentPage.setPageNumber(1);
        contentPage.setType(StoryPageType.CONTENT);
        contentPage.setImageUrl("http://example.com/content.png");
        contentPage.setEnglishSummary("A page with content.");
        contentPage.setParagraphs(List.of(paragraph1));
        contentPage.setVocabulary(List.of(vocabItem1));

        StoryVocabularyItem vocabItem2 = new StoryVocabularyItem();
        vocabItem2.setWord("batch");
        vocabItem2.setTranslation("a group of things");
        vocabItem2.setPageNumber(2);

        StoryPage vocabPage = new StoryPage();
        vocabPage.setPageNumber(2);
        vocabPage.setType(StoryPageType.VOCABULARY);
        vocabPage.setImageUrl("http://example.com/vocab.png");
        vocabPage.setEnglishSummary("A page with vocabulary.");
        vocabPage.setVocabulary(List.of(vocabItem2));

        List<StoryPage> pagesToInsert = List.of(contentPage, vocabPage);

        // 2. Act
        storyPageRepository.batchInsertPages(managedShortStory, pagesToInsert);

        // 3. Assert
        List<Record> insertedPages = dsl.select().from(STORY_PAGE)
                .where(STORY_PAGE.SHORT_STORY_ID.eq(managedShortStory.getId()))
                .orderBy(STORY_PAGE.PAGE_NUMBER)
                .fetch();

        assertThat(insertedPages).hasSize(2);

        // Assert Content Page
        Record contentPageRecord = insertedPages.get(0);
        Long contentPageId = contentPageRecord.get(STORY_PAGE.ID);
        assertThat(contentPageRecord.get(STORY_PAGE.PAGE_NUMBER)).isEqualTo(contentPage.getPageNumber());
        assertThat(StoryPageType.valueOf(contentPageRecord.get(STORY_PAGE.TYPE))).isEqualTo(contentPage.getType());
        assertThat(contentPageRecord.get(STORY_PAGE.IMAGE_URL)).isEqualTo(contentPage.getImageUrl());
        assertThat(contentPageRecord.get(STORY_PAGE.ENGLISH_SUMMARY)).isEqualTo(contentPage.getEnglishSummary());

        // Assert Paragraphs for Content Page
        List<Map<String, Object>> insertedParagraphs = dsl.select().from(STORY_PARAGRAPH)
                .where(STORY_PARAGRAPH.STORY_PAGE_ID.eq(contentPageId))
                .fetchMaps();
        assertThat(insertedParagraphs).hasSize(1);
        Map<String, Object> paragraphRecord = insertedParagraphs.get(0);
        assertThat(paragraphRecord.get("paragraph_number")).isEqualTo(paragraph1.getParagraphNumber());
        assertThat(paragraphRecord.get("content")).isEqualTo(paragraph1.getContent());

        // Assert Vocabulary for Content Page
        List<Map<String, Object>> insertedContentVocab = dsl.select().from(STORY_VOCABULARY_ITEM)
                .where(STORY_VOCABULARY_ITEM.STORY_PAGE_ID.eq(contentPageId))
                .fetchMaps();
        assertThat(insertedContentVocab).hasSize(1);
        Map<String, Object> contentVocabRecord = insertedContentVocab.get(0);
        assertThat(contentVocabRecord.get("word")).isEqualTo(vocabItem1.getWord());
        assertThat(contentVocabRecord.get("translation")).isEqualTo(vocabItem1.getTranslation());
        assertThat(contentVocabRecord.get("page_number")).isEqualTo(vocabItem1.getPageNumber());

        // Assert Vocab Page
        Record vocabPageRecord = insertedPages.get(1);
        Long vocabPageId = vocabPageRecord.get(STORY_PAGE.ID);
        assertThat(vocabPageRecord.get(STORY_PAGE.PAGE_NUMBER)).isEqualTo(vocabPage.getPageNumber());
        assertThat(StoryPageType.valueOf(vocabPageRecord.get(STORY_PAGE.TYPE))).isEqualTo(vocabPage.getType());
        assertThat(vocabPageRecord.get(STORY_PAGE.IMAGE_URL)).isEqualTo(vocabPage.getImageUrl());
        assertThat(vocabPageRecord.get(STORY_PAGE.ENGLISH_SUMMARY)).isEqualTo(vocabPage.getEnglishSummary());

        // Assert Vocabulary for Vocab Page
        List<Map<String, Object>> insertedVocabVocab = dsl.select().from(STORY_VOCABULARY_ITEM)
                .where(STORY_VOCABULARY_ITEM.STORY_PAGE_ID.eq(vocabPageId))
                .fetchMaps();
        assertThat(insertedVocabVocab).hasSize(1);
        Map<String, Object> vocabVocabRecord = insertedVocabVocab.get(0);
        assertThat(vocabVocabRecord.get("word")).isEqualTo(vocabItem2.getWord());
        assertThat(vocabVocabRecord.get("translation")).isEqualTo(vocabItem2.getTranslation());
        assertThat(vocabVocabRecord.get("page_number")).isEqualTo(vocabItem2.getPageNumber());

        // Final logging for confirmation
        log.info("Final state of inserted pages:\n{}", insertedPages);
        log.info("Final state of content page paragraphs:\n{}", insertedParagraphs);
        log.info("Final state of content page vocabulary:\n{}", insertedContentVocab);
        log.info("Final state of vocabulary page vocabulary:\n{}", insertedVocabVocab);
    }
}
