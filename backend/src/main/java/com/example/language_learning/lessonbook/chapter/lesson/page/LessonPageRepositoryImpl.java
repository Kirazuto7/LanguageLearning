package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.generated.jooq.tables.records.*;
import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.data.*;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestion;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExample;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentence;
import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.shared.word.data.WordDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.example.language_learning.generated.jooq.tables.ConjugationExample.CONJUGATION_EXAMPLE;
import static com.example.language_learning.generated.jooq.tables.ConjugationLesson.CONJUGATION_LESSON;
import static com.example.language_learning.generated.jooq.tables.ConjugationLessonExample.CONJUGATION_LESSON_EXAMPLE;
import static com.example.language_learning.generated.jooq.tables.GrammarLesson.GRAMMAR_LESSON;
import static com.example.language_learning.generated.jooq.tables.GrammarLessonSentence.GRAMMAR_LESSON_SENTENCE;
import static com.example.language_learning.generated.jooq.tables.Lesson.LESSON;
import static com.example.language_learning.generated.jooq.tables.LessonPage.LESSON_PAGE;
import static com.example.language_learning.generated.jooq.tables.LessonQuestion.LESSON_QUESTION;
import static com.example.language_learning.generated.jooq.tables.PracticeLesson.PRACTICE_LESSON;
import static com.example.language_learning.generated.jooq.tables.QuestionAnswerChoice.QUESTION_ANSWER_CHOICE;
import static com.example.language_learning.generated.jooq.tables.ReadingComprehensionLesson.READING_COMPREHENSION_LESSON;
import static com.example.language_learning.generated.jooq.tables.Sentence.SENTENCE;
import static com.example.language_learning.generated.jooq.tables.VocabularyLesson.VOCABULARY_LESSON;
import static com.example.language_learning.generated.jooq.tables.VocabularyLessonWord.VOCABULARY_LESSON_WORD;
import static com.example.language_learning.generated.jooq.tables.Word.WORD;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LessonPageRepositoryImpl implements LessonPageRepositoryCustom {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void batchInsertPages(LessonChapter chapter, List<LessonPage> lessonPages) {

        if (lessonPages == null || lessonPages.isEmpty()) {
            return;
        }

        List<LessonPageRecord> pagesToInsert = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // 1. Iterate through each page, persist its lesson content, and prepare the page record.
        for (LessonPage page : lessonPages) {
            Lesson lesson = page.getLesson();
            Long newLessonId = null;

            switch (lesson.getType()) {
                case VOCABULARY -> newLessonId = persistVocabularyLesson((VocabularyLesson) lesson, now);
                case GRAMMAR -> newLessonId = persistGrammarLesson((GrammarLesson) lesson, now);
                case CONJUGATION -> newLessonId = persistConjugationLesson((ConjugationLesson) lesson, now);
                case PRACTICE -> newLessonId = persistPracticeLesson((PracticeLesson) lesson, now);
                case READING_COMPREHENSION -> newLessonId = persistReadingLesson((ReadingComprehensionLesson) lesson, now);
            }

            if (newLessonId != null) {
                LessonPageRecord pageRecord = dsl.newRecord(LESSON_PAGE);
                pageRecord.setLessonChapterId(chapter.getId());
                pageRecord.setLessonId(newLessonId);
                pageRecord.setCreatedAt(now);
                pagesToInsert.add(pageRecord);
            }
        }

        // 2. Batch insert the pages
        if (!pagesToInsert.isEmpty()) {
            dsl.batchInsert(pagesToInsert).execute();
        }
    }

    private Long persistBaseLesson(Lesson lesson, LocalDateTime timestamp) {
        LessonRecord newLessonRecord = dsl.insertInto(LESSON, LESSON.TITLE, LESSON.TYPE, LESSON.CREATED_AT)
                .values(lesson.getTitle(), lesson.getType().name(), timestamp)
                .returning(LESSON.ID)
                .fetchOne();
        return (newLessonRecord != null) ? newLessonRecord.getId() : null;
    }

    private Long persistVocabularyLesson(VocabularyLesson lesson, LocalDateTime timestamp) {
        Long newId = persistBaseLesson(lesson, timestamp);
        if (newId == null) {
            log.error("Failed to insert base lesson for VocabularyLesson");
            return null;
        }

        dsl.insertInto(VOCABULARY_LESSON, VOCABULARY_LESSON.ID)
            .values(newId)
            .execute();

        List<Word> wordsToPersist = lesson.getVocabularies();
        if (wordsToPersist != null && !wordsToPersist.isEmpty()) {
            Map<Word, Long> persistedWordsMap = persistWords(wordsToPersist, timestamp);
            batchInsertVocabularyLessonWords(newId, persistedWordsMap);
        }
        return newId;
    }

    private Long persistGrammarLesson(GrammarLesson lesson, LocalDateTime timestamp) {
        Long newId = persistBaseLesson(lesson, timestamp);
        if (newId == null) {
            log.error("Failed to insert base lesson for GrammarLesson");
            return null;
        }

        dsl.insertInto(GRAMMAR_LESSON, GRAMMAR_LESSON.ID, GRAMMAR_LESSON.GRAMMAR_CONCEPT, GRAMMAR_LESSON.EXPLANATION)
            .values(newId, lesson.getGrammarConcept(), lesson.getExplanation())
            .execute();

        List<LessonSentence> sentencesToPersist = lesson.getExampleLessonSentences();
        if (sentencesToPersist != null && !sentencesToPersist.isEmpty()) {
            Map<LessonSentence, Long> persistedSentencesMap = persistLessonSentences(sentencesToPersist, timestamp);
            batchInsertGrammarLessonSentences(newId, persistedSentencesMap);
        }
        return newId;
    }

    private Long persistConjugationLesson(ConjugationLesson lesson, LocalDateTime timestamp) {
        Long newId = persistBaseLesson(lesson, timestamp);
        if (newId == null) {
            log.error("Failed to insert base lesson for ConjugationLesson");
            return null;
        }

        dsl.insertInto(CONJUGATION_LESSON, CONJUGATION_LESSON.ID, CONJUGATION_LESSON.CONJUGATION_RULE_NAME, CONJUGATION_LESSON.EXPLANATION)
            .values(newId, lesson.getConjugationRuleName(), lesson.getExplanation())
            .execute();

        List<LessonConjugationExample> examplesToPersist = lesson.getConjugatedWords();
        if (examplesToPersist != null && !examplesToPersist.isEmpty()) {
            Map<LessonConjugationExample, Long> persistedSentencesMap = persistConjugationExamples(examplesToPersist, timestamp);
            batchInsertConjugationLessonExamples(newId, persistedSentencesMap);
        }
        return newId;
    }

    private Long persistPracticeLesson(PracticeLesson lesson, LocalDateTime timestamp) {
        Long newId = persistBaseLesson(lesson, timestamp);
        if (newId == null) {
            log.error("Failed to insert base lesson for PracticeLesson");
            return null;
        }

        dsl.insertInto(PRACTICE_LESSON, PRACTICE_LESSON.ID, PRACTICE_LESSON.INSTRUCTIONS)
            .values(newId, lesson.getInstructions())
            .execute();

        List<LessonQuestion> questionsToPersist = lesson.getLessonQuestions();
        if (questionsToPersist != null && !questionsToPersist.isEmpty()) {
            persistLessonQuestions(newId, questionsToPersist, timestamp);
        }
        return newId;
    }

    private Long persistReadingLesson(ReadingComprehensionLesson lesson, LocalDateTime timestamp) {
        Long newId = persistBaseLesson(lesson, timestamp);
        if (newId == null) {
            log.error("Failed to insert base lesson for ReadingComprehensionLesson");
            return null;
        }

        dsl.insertInto(READING_COMPREHENSION_LESSON, READING_COMPREHENSION_LESSON.ID, READING_COMPREHENSION_LESSON.STORY)
                .values(newId, lesson.getStory())
                .execute();

        List<LessonQuestion> questionsToPersist = lesson.getLessonQuestions();
        if (questionsToPersist != null && !questionsToPersist.isEmpty()) {
            persistLessonQuestions(newId, questionsToPersist, timestamp);
        }
        return newId;
    }

    // =============================================================
    // Helper Methods
    // =============================================================

    /**
     * Persists a list of new Word entities one by one, using a concurrent-safe
     * INSERT...RETURNING statement for each.
     * @param words A list of new, un-persisted Word entities.
     * @return A map linking each original Word object to its new database ID.
     */
    private Map<Word, Long> persistWords(List<Word> words, LocalDateTime timestamp) {
        if (words == null || words.isEmpty()) {
            return new HashMap<>();
        }

        Map<Word, Long> persistedWordIds = new HashMap<>();

        for (Word word : words) {
            JSONB detailsJsonb = JSONB.valueOf(serializeWordDetails(word.getDetails()));

            WordRecord newWordRecord = dsl.insertInto(
                WORD,
                WORD.ENGLISH_TRANSLATION,
                WORD.LANGUAGE,
                WORD.DETAILS,
                WORD.CREATED_AT
            )
            .values(word.getEnglishTranslation(), word.getLanguage(), detailsJsonb, timestamp)
            .returning(WORD.ID)
            .fetchOne();

            if (newWordRecord != null) {
                persistedWordIds.put(word, newWordRecord.getId());
            }
        }
        return persistedWordIds;
    }

    /**
     * Performs a batch insert into the vocabulary_lesson_words join table.
     */
    private void batchInsertVocabularyLessonWords(Long lessonId, Map<Word, Long> persistedWordsMap) {
        if (persistedWordsMap == null || persistedWordsMap.isEmpty()) {
            return;
        }

        InsertValuesStep2<VocabularyLessonWordRecord, Long, Long> template = dsl.insertInto(
            VOCABULARY_LESSON_WORD,
            VOCABULARY_LESSON_WORD.LESSON_ID,
            VOCABULARY_LESSON_WORD.WORD_ID
        ).values((Long) null, (Long) null);

        BatchBindStep batch = dsl.batch(template);
        for (Long wordId : persistedWordsMap.values()) {
            batch.bind(lessonId, wordId);
        }
        batch.execute();
    }

    /**
     * Serializes the WordDetails object into a JSON string.
     */
    private String serializeWordDetails(WordDetails details) {
        if (details == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(details);
        }
        catch (JsonProcessingException e) {
            log.error("Error serializing word details to JSON", e);
            return "{}";
        }
    }

    /**
     * Persists a list of new LessonSentence entities one by one, using a concurrent-safe
     * INSERT...RETURNING statement for each.
     */
    private Map<LessonSentence, Long> persistLessonSentences(List<LessonSentence> sentences, LocalDateTime timestamp) {
        if (sentences == null || sentences.isEmpty()) {
            return new HashMap<>();
        }

        Map<LessonSentence, Long> persistedSentenceIds = new HashMap<>();

        for (LessonSentence sentence : sentences) {
            SentenceRecord newSentenceRecord = dsl.insertInto(
                            SENTENCE,
                            SENTENCE.TEXT,
                            SENTENCE.TRANSLATION,
                            SENTENCE.CREATED_AT
                    )
                    .values(
                            sentence.getText(),
                            sentence.getTranslation(),
                            timestamp
                    )
                    .returning(SENTENCE.ID)
                    .fetchOne();

            if (newSentenceRecord != null) {
                persistedSentenceIds.put(sentence, newSentenceRecord.getId());
            }
        }
        return persistedSentenceIds;
    }

    /**
     * Performs a batch insert into the grammar_lesson_sentences join table.
     */
    private void batchInsertGrammarLessonSentences(Long lessonId, Map<LessonSentence, Long> persistedSentencesMap) {
        if (persistedSentencesMap == null || persistedSentencesMap.isEmpty()) {
            return;
        }

        InsertValuesStep2<GrammarLessonSentenceRecord, Long, Long> template = dsl.insertInto(
                GRAMMAR_LESSON_SENTENCE,
                GRAMMAR_LESSON_SENTENCE.LESSON_ID,
                GRAMMAR_LESSON_SENTENCE.SENTENCE_ID
        ).values((Long) null, (Long) null);

        BatchBindStep batch = dsl.batch(template);
        for (Long sentenceId : persistedSentencesMap.values()) {
            batch.bind(lessonId, sentenceId);
        }
        batch.execute();
    }

    /**
     * Persists a list of new LessonConjugationExample entities one by one, using a concurrent-safe
     * INSERT...RETURNING statement for each.
     */
    private Map<LessonConjugationExample, Long> persistConjugationExamples(List<LessonConjugationExample> examples, LocalDateTime timestamp) {
        if (examples == null || examples.isEmpty()) {
            return new HashMap<>();
        }

        Map<LessonConjugationExample, Long> persistedExampleIds = new HashMap<>();

        for (LessonConjugationExample example : examples) {
            // Assuming the table is named CONJUGATION_EXAMPLES from your jOOQ generation
            ConjugationExampleRecord newExampleRecord = dsl.insertInto(
                            CONJUGATION_EXAMPLE,
                            CONJUGATION_EXAMPLE.INFINITIVE,
                            CONJUGATION_EXAMPLE.CONJUGATED_FORM,
                            CONJUGATION_EXAMPLE.EXAMPLE_SENTENCE,
                            CONJUGATION_EXAMPLE.SENTENCE_TRANSLATION,
                            CONJUGATION_EXAMPLE.CREATED_AT
                    )
                    .values(
                            example.getInfinitive(),
                            example.getConjugatedForm(),
                            example.getExampleSentence(),
                            example.getSentenceTranslation(),
                            timestamp
                    )
                    .returning(CONJUGATION_EXAMPLE.ID)
                    .fetchOne();

            if (newExampleRecord != null) {
                persistedExampleIds.put(example, newExampleRecord.getId());
            }
        }
        return persistedExampleIds;
    }

    /**
     * Performs a batch insert into the conjugation_lesson_examples join table.
     */
    private void batchInsertConjugationLessonExamples(Long lessonId, Map<LessonConjugationExample, Long> persistedExamplesMap) {
        if (persistedExamplesMap == null || persistedExamplesMap.isEmpty()) {
            return;
        }

        // Assuming the join table is named CONJUGATION_LESSON_EXAMPLES from your jOOQ generation
        InsertValuesStep2<ConjugationLessonExampleRecord, Long, Long> template = dsl.insertInto(
                CONJUGATION_LESSON_EXAMPLE,
                CONJUGATION_LESSON_EXAMPLE.LESSON_ID,
                CONJUGATION_LESSON_EXAMPLE.EXAMPLE_ID
        ).values((Long) null, (Long) null);

        BatchBindStep batch = dsl.batch(template);
        for (Long exampleId : persistedExamplesMap.values()) {
            batch.bind(lessonId, exampleId);
        }
        batch.execute();
    }

    /**
     * Persists a list of new LessonQuestion entities and their answer choices
     * by looping and using concurrent-safe INSERT...RETURNING statements.
     */
    private void persistLessonQuestions(Long lessonId, List<LessonQuestion> questions, LocalDateTime timestamp) {
        if (questions == null || questions.isEmpty()) {
            return;
        }

        for (LessonQuestion question : questions) {
            // Insert the question and get its new ID.
            LessonQuestionRecord newQuestionRecord = dsl.insertInto(
                            LESSON_QUESTION,
                            LESSON_QUESTION.LESSON_ID,
                            LESSON_QUESTION.QUESTION_TYPE,
                            LESSON_QUESTION.QUESTION_TEXT,
                            LESSON_QUESTION.ANSWER,
                            LESSON_QUESTION.CREATED_AT
                    )
                    .values(
                            lessonId,
                            question.getQuestionType().name(),
                            question.getQuestionText(),
                            question.getAnswer(),
                            timestamp
                    )
                    .returning(LESSON_QUESTION.ID)
                    .fetchOne();

            if (newQuestionRecord != null) {
                Long newQuestionId = newQuestionRecord.getId();
                // If the question has answer choices, batch insert them now.
                if (question.getAnswerChoices() != null && !question.getAnswerChoices().isEmpty()) {
                    batchInsertAnswerChoices(newQuestionId, question.getAnswerChoices());
                }
            }
        }
    }


    /**
     * Performs a batch insert into the question_answer_choices table for a single question.
     */
    private void batchInsertAnswerChoices(Long questionId, List<String> choices) {
        if (choices == null || choices.isEmpty()) {
            return;
        }

        InsertValuesStep2<QuestionAnswerChoiceRecord, Long, String> template = dsl.insertInto(
                QUESTION_ANSWER_CHOICE,
                QUESTION_ANSWER_CHOICE.QUESTION_ID,
                QUESTION_ANSWER_CHOICE.ANSWER_CHOICE
        ).values((Long) null, (String) null);

        BatchBindStep batch = dsl.batch(template);
        for (String choice : choices) {
            batch.bind(questionId, choice);
        }
        batch.execute();
    }
}
