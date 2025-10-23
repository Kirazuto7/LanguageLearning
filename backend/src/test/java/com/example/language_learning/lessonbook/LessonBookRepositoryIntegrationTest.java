package com.example.language_learning.lessonbook;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.data.*;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.shared.services.ImageService;
import com.example.language_learning.shared.enums.LessonType;
import com.example.language_learning.user.User;
import com.example.language_learning.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JSONB;
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
import com.example.language_learning.shared.word.data.JapaneseWordDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

import static com.example.language_learning.generated.jooq.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Slf4j
public class LessonBookRepositoryIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AIEngine aiEngine() {
            return Mockito.mock(AIEngine.class);
        }

        @Bean
        @Primary
        public ImageService imageService() {
            return Mockito.mock(ImageService.class);
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
    private LessonBookRepository lessonBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findDetailsByUserAndLanguageAndDifficulty_shouldReturnCorrectLessonBook() throws JsonProcessingException {
        // 1. Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user = userRepository.save(user);

        Long lessonBookId = dsl.insertInto(LESSON_BOOK)
                .set(LESSON_BOOK.USER_ID, user.getId())
                .set(LESSON_BOOK.TITLE, "Japanese for A1 Learners")
                .set(LESSON_BOOK.LANGUAGE, "ja")
                .set(LESSON_BOOK.DIFFICULTY, "A1")
                .returning(LESSON_BOOK.ID)
                .fetchOne()
                .getId();

        Long lessonChapterId = dsl.insertInto(LESSON_CHAPTER)
                .set(LESSON_CHAPTER.BOOK_ID, lessonBookId)
                .set(LESSON_CHAPTER.TITLE, "Greetings")
                .set(LESSON_CHAPTER.NATIVE_TITLE, "挨拶")
                .returning(LESSON_CHAPTER.ID)
                .fetchOne()
                .getId();

        // --- Page 1: Vocabulary Lesson ---
        Long vocabLessonId = dsl.insertInto(LESSON)
                .set(LESSON.TYPE, LessonType.VOCABULARY.name())
                .returning(LESSON.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(VOCABULARY_LESSON)
                .set(VOCABULARY_LESSON.ID, vocabLessonId)
                .execute();

        JapaneseWordDetails wordDetails = JapaneseWordDetails.builder()
                .hiragana("こんにちは")
                .build();

        Long wordId = dsl.insertInto(WORD)
                .set(WORD.LANGUAGE, "ja")
                .set(WORD.DETAILS, JSONB.valueOf(objectMapper.writeValueAsString(wordDetails)))
                .set(WORD.ENGLISH_TRANSLATION, "Hello")
                .returning(WORD.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(VOCABULARY_LESSON_WORD)
                .set(VOCABULARY_LESSON_WORD.LESSON_ID, vocabLessonId)
                .set(VOCABULARY_LESSON_WORD.WORD_ID, wordId)
                .execute();
        dsl.insertInto(LESSON_PAGE)
                .set(LESSON_PAGE.LESSON_CHAPTER_ID, lessonChapterId)
                .set(LESSON_PAGE.LESSON_ID, vocabLessonId)
                .execute();

        // --- Page 2: Grammar Lesson ---
        Long grammarLessonId = dsl.insertInto(LESSON)
                .set(LESSON.TYPE, LessonType.GRAMMAR.name())
                .returning(LESSON.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(GRAMMAR_LESSON)
                .set(GRAMMAR_LESSON.ID, grammarLessonId)
                .set(GRAMMAR_LESSON.GRAMMAR_CONCEPT, "Topic Marker は (wa)")
                .set(GRAMMAR_LESSON.EXPLANATION, "The particle は is used to mark the topic of a sentence.")
                .execute();
        Long sentenceId = dsl.insertInto(SENTENCE)
                .set(SENTENCE.TEXT, "猫はかわいいです")
                .set(SENTENCE.TRANSLATION, "Cats are cute")
                .returning(SENTENCE.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(GRAMMAR_LESSON_SENTENCE)
                .set(GRAMMAR_LESSON_SENTENCE.LESSON_ID, grammarLessonId)
                .set(GRAMMAR_LESSON_SENTENCE.SENTENCE_ID, sentenceId)
                .execute();
        dsl.insertInto(LESSON_PAGE)
                .set(LESSON_PAGE.LESSON_CHAPTER_ID, lessonChapterId)
                .set(LESSON_PAGE.LESSON_ID, grammarLessonId)
                .execute();

        // --- Page 3: Practice Lesson ---
        Long practiceLessonId = dsl.insertInto(LESSON)
                .set(LESSON.TYPE, LessonType.PRACTICE.name())
                .returning(LESSON.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(PRACTICE_LESSON)
                .set(PRACTICE_LESSON.ID, practiceLessonId)
                .set(PRACTICE_LESSON.INSTRUCTIONS, "Translate the following sentences.")
                .execute();
        dsl.insertInto(LESSON_QUESTION)
                .set(LESSON_QUESTION.LESSON_ID, practiceLessonId)
                .set(LESSON_QUESTION.QUESTION_TEXT, "How do you say 'Hello'?")
                .set(LESSON_QUESTION.ANSWER, "こんにちは")
                .execute();
        dsl.insertInto(LESSON_PAGE)
                .set(LESSON_PAGE.LESSON_CHAPTER_ID, lessonChapterId)
                .set(LESSON_PAGE.LESSON_ID, practiceLessonId)
                .execute();

        // --- Page 4: Conjugation Lesson ---
        Long conjugationLessonId = dsl.insertInto(LESSON)
                .set(LESSON.TYPE, LessonType.CONJUGATION.name())
                .returning(LESSON.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(CONJUGATION_LESSON)
                .set(CONJUGATION_LESSON.ID, conjugationLessonId)
                .set(CONJUGATION_LESSON.CONJUGATION_RULE_NAME, "Masu-form")
                .set(CONJUGATION_LESSON.EXPLANATION, "The polite form of verbs.")
                .execute();
        dsl.insertInto(CONJUGATION_EXAMPLE)
                .set(CONJUGATION_EXAMPLE.INFINITIVE, "食べる")
                .set(CONJUGATION_EXAMPLE.CONJUGATED_FORM, "食べます")
                .execute();
        dsl.insertInto(LESSON_PAGE)
                .set(LESSON_PAGE.LESSON_CHAPTER_ID, lessonChapterId)
                .set(LESSON_PAGE.LESSON_ID, conjugationLessonId)
                .execute();

        // --- Page 5: Reading Comprehension Lesson ---
        Long readingLessonId = dsl.insertInto(LESSON)
                .set(LESSON.TYPE, LessonType.READING_COMPREHENSION.name())
                .returning(LESSON.ID)
                .fetchOne()
                .getId();
        dsl.insertInto(READING_COMPREHENSION_LESSON)
                .set(READING_COMPREHENSION_LESSON.ID, readingLessonId)
                .set(READING_COMPREHENSION_LESSON.STORY, "猫は公園にいます。")
                .execute();
        dsl.insertInto(LESSON_QUESTION)
                .set(LESSON_QUESTION.LESSON_ID, readingLessonId)
                .set(LESSON_QUESTION.QUESTION_TEXT, "Where is the cat?")
                .set(LESSON_QUESTION.ANSWER, "The park")
                .execute();
        dsl.insertInto(LESSON_PAGE)
                .set(LESSON_PAGE.LESSON_CHAPTER_ID, lessonChapterId)
                .set(LESSON_PAGE.LESSON_ID, readingLessonId)
                .execute();

        // 2. Act
        Optional<LessonBook> result = lessonBookRepository.findDetailsByUserAndLanguageAndDifficulty(user, "ja", "A1");

        // 3. Assert
        assertThat(result).isPresent();
        LessonBook foundBook = result.get();

        // Assert top-level LessonBook fields
        assertThat(foundBook.getTitle()).isEqualTo("Japanese for A1 Learners");
        assertThat(foundBook.getLanguage()).isEqualTo("ja");
        assertThat(foundBook.getDifficulty()).isEqualTo("A1");
        assertThat(foundBook.getUser().getId()).isEqualTo(user.getId());
        assertThat(foundBook.getLessonChapters()).isNotNull().hasSize(1);

        // Assert nested LessonChapter fields
        LessonChapter foundChapter = foundBook.getLessonChapters().get(0);
        assertThat(foundChapter.getTitle()).isEqualTo("Greetings");
        assertThat(foundChapter.getNativeTitle()).isEqualTo("挨拶");
        assertThat(foundChapter.getLessonPages()).isNotNull().hasSize(5);

        // --- Assert Page 1: Vocabulary Lesson ---
        LessonPage vocabPage = foundChapter.getLessonPages().get(0);
        assertThat(vocabPage.getLesson()).isNotNull().isInstanceOf(VocabularyLesson.class);
        VocabularyLesson vocabLesson = (VocabularyLesson) vocabPage.getLesson();
        assertThat(vocabLesson.getVocabularies()).isNotNull().hasSize(1);
        assertThat(vocabLesson.getVocabularies().get(0).getDetails()).isInstanceOf(JapaneseWordDetails.class);
        JapaneseWordDetails foundDetails = (JapaneseWordDetails) vocabLesson.getVocabularies().get(0).getDetails();
        assertThat(foundDetails.hiragana()).isEqualTo("こんにちは");

        // --- Assert Page 2: Grammar Lesson ---
        LessonPage grammarPage = foundChapter.getLessonPages().get(1);
        assertThat(grammarPage.getLesson()).isNotNull().isInstanceOf(GrammarLesson.class);
        GrammarLesson grammarLesson = (GrammarLesson) grammarPage.getLesson();
        assertThat(grammarLesson.getGrammarConcept()).isEqualTo("Topic Marker は (wa)");
        assertThat(grammarLesson.getExampleLessonSentences()).isNotNull().hasSize(1);
        assertThat(grammarLesson.getExampleLessonSentences().get(0).getText()).isEqualTo("猫はかわいいです");

        // --- Assert Page 3: Practice Lesson ---
        LessonPage practicePage = foundChapter.getLessonPages().get(2);
        assertThat(practicePage.getLesson()).isNotNull().isInstanceOf(PracticeLesson.class);
        PracticeLesson practiceLesson = (PracticeLesson) practicePage.getLesson();
        assertThat(practiceLesson.getInstructions()).isEqualTo("Translate the following sentences.");
        assertThat(practiceLesson.getLessonQuestions()).isNotNull().hasSize(1);
        assertThat(practiceLesson.getLessonQuestions().get(0).getQuestionText()).isEqualTo("How do you say 'Hello'?");

        // --- Assert Page 4: Conjugation Lesson ---
        LessonPage conjugationPage = foundChapter.getLessonPages().get(3);
        assertThat(conjugationPage.getLesson()).isNotNull().isInstanceOf(ConjugationLesson.class);
        ConjugationLesson conjugationLesson = (ConjugationLesson) conjugationPage.getLesson();
        assertThat(conjugationLesson.getConjugationRuleName()).isEqualTo("Masu-form");
        assertThat(conjugationLesson.getConjugatedWords()).isNotNull().hasSize(1);
        assertThat(conjugationLesson.getConjugatedWords().get(0).getConjugatedForm()).isEqualTo("食べます");

        // --- Assert Page 5: Reading Comprehension Lesson ---
        LessonPage readingPage = foundChapter.getLessonPages().get(4);
        assertThat(readingPage.getLesson()).isNotNull().isInstanceOf(ReadingComprehensionLesson.class);
        ReadingComprehensionLesson readingLesson = (ReadingComprehensionLesson) readingPage.getLesson();
        assertThat(readingLesson.getStory()).isEqualTo("猫は公園にいます。");
        assertThat(readingLesson.getLessonQuestions()).isNotNull().hasSize(1);
        assertThat(readingLesson.getLessonQuestions().get(0).getQuestionText()).isEqualTo("Where is the cat?");

        // Final logging for confirmation
        log.info("Final fetched LessonBook object:\n{}", foundBook);
    }
}