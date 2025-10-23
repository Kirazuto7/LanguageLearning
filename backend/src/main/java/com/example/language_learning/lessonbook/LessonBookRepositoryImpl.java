package com.example.language_learning.lessonbook;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.data.*;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestion;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExample;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentence;
import com.example.language_learning.shared.enums.LessonType;
import com.example.language_learning.shared.enums.QuestionType;
import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.shared.word.data.WordDetails;
import com.example.language_learning.user.User;
import com.example.language_learning.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.language_learning.generated.jooq.Tables.*;
import static com.example.language_learning.generated.jooq.tables.LessonBook.LESSON_BOOK;
import static com.example.language_learning.generated.jooq.tables.LessonChapter.LESSON_CHAPTER;
import static com.example.language_learning.generated.jooq.tables.LessonPage.LESSON_PAGE;
import static com.example.language_learning.generated.jooq.tables.VocabularyLesson.VOCABULARY_LESSON;
import static org.jooq.impl.DSL.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LessonBookRepositoryImpl implements LessonBookRepositoryCustom {

    private final DSLContext dsl;
    private final UserRepository userRepository;

    @Override
    public Optional<LessonBook> findDetailsById(Long id, User user) {
        return selectLessonBookDetails()
         .where(LESSON_BOOK.ID.eq(id))
         .and(LESSON_BOOK.USER_ID.eq(user.getId()))
         .fetchOptional(this::mapToLessonBook);
    }

    @Override
    public Optional<LessonBook> findDetailsByUserAndLanguageAndDifficulty(User user, String language, String difficulty) {
        return selectLessonBookDetails()
        .where(LESSON_BOOK.USER_ID.eq(user.getId()))
        .and(LESSON_BOOK.LANGUAGE.eq(language))
        .and(LESSON_BOOK.DIFFICULTY.eq(difficulty))
        .fetchOptional(this::mapToLessonBook);
    }

    @Override
    @Transactional
    public int deleteLessonBookById(Long lessonBookId, User user) {
        var chapters = name("chapters").as(
            select(LESSON_CHAPTER.ID).from(LESSON_CHAPTER)
                    .where(LESSON_CHAPTER.BOOK_ID.eq(lessonBookId))
        );

        var pages = name("pages").as(
            select(LESSON_PAGE.ID, LESSON_PAGE.LESSON_ID).from(LESSON_PAGE)
                    .where(LESSON_PAGE.LESSON_CHAPTER_ID.in(
                        select(field("id", Long.class)).from(chapters)
                    ))
        );

        var lessons = name("lessons").as(
            select(field("lesson_id", Long.class)).from(pages)
        );

        // TODO: FETCH LIST OF LESSON IDS TO DELETE

        var questions = name("questions").as(
            select(LESSON_QUESTION.ID).from(LESSON_QUESTION)
                    .where(LESSON_QUESTION.LESSON_ID.in(
                        select(field("lesson_id", Long.class)).from(lessons)
                    ))
        );
        
        deleteGrandchildren(chapters, pages, lessons, questions);
        deleteChildren(lessonBookId, chapters, pages, lessons);

        int deletedRows = dsl.deleteFrom(LESSON_BOOK)
            .where(LESSON_BOOK.ID.eq(lessonBookId))
            .and(LESSON_BOOK.USER_ID.eq(user.getId()))
            .execute();
        log.info("Deleted lesson book with id: {}", lessonBookId);
        return deletedRows;
    }

    private LessonBook mapToLessonBook(Record r) {
        LessonBook lessonBook = new LessonBook();
        lessonBook.setId(r.get(LESSON_BOOK.ID));
        lessonBook.setTitle(r.get(LESSON_BOOK.TITLE));
        lessonBook.setDifficulty(r.get(LESSON_BOOK.DIFFICULTY));
        lessonBook.setLanguage(r.get(LESSON_BOOK.LANGUAGE));
        lessonBook.setCreatedAt(r.get(LESSON_BOOK.CREATED_AT));
        Long userId = r.get(LESSON_BOOK.USER_ID);
        userRepository.findById(userId).ifPresent(lessonBook::setUser);

        Result<Record> chapterRecords = r.get("lessonChapters", Result.class);
        List<LessonChapter> chapters = new ArrayList<>();
        for (Record cr : chapterRecords) {
            LessonChapter chapter = new LessonChapter();
            chapter.setId(cr.get(LESSON_CHAPTER.ID));
            chapter.setTitle(cr.get(LESSON_CHAPTER.TITLE));
            chapter.setNativeTitle(cr.get(LESSON_CHAPTER.NATIVE_TITLE));

            Result<Record> pageRecords = cr.get("lessonPages", Result.class);
            List<LessonPage> pages = new ArrayList<>();
            for (Record pr: pageRecords) {
                LessonPage page = new LessonPage();
                page.setId(pr.get(LESSON_PAGE.ID));

                LessonType lessonType = pr.get(LESSON.TYPE, LessonType.class);
                String lessonTitle = pr.get(LESSON.TITLE);
                switch (lessonType) {
                    case VOCABULARY -> {
                        Result<Record> lessonRecords = pr.get("vocabularyLesson", Result.class);
                        if (!lessonRecords.isEmpty()) {
                            Record lr = lessonRecords.getFirst();
                            VocabularyLesson lesson = new VocabularyLesson();
                            lesson.setId(lr.get(VOCABULARY_LESSON.ID));
                            lesson.setType(lessonType);
                            lesson.setTitle(lessonTitle);
                            Result<Record> wordRecords = lr.get("vocabularies", Result.class);
                            List<Word> words = new ArrayList<>();
                            for (Record wr : wordRecords) {
                                Word word = new Word();
                                word.setId(wr.get(WORD.ID));
                                word.setLanguage(wr.get(WORD.LANGUAGE));
                                word.setEnglishTranslation(wr.get(WORD.ENGLISH_TRANSLATION));
                                word.setDetails(wr.get(WORD.DETAILS, WordDetails.class));
                                words.add(word);
                            }
                            lesson.setVocabularies(words);
                            page.setLesson(lesson);
                            lesson.setLessonPage(page);
                        }
                    }
                    case GRAMMAR -> {
                        Result<Record> lessonRecords = pr.get("grammarLesson", Result.class);
                        if (!lessonRecords.isEmpty()) {
                            Record lr = lessonRecords.getFirst();
                            GrammarLesson lesson = new GrammarLesson();
                            lesson.setId(lr.get(GRAMMAR_LESSON.ID));
                            lesson.setType(lessonType);
                            lesson.setTitle(lessonTitle);
                            lesson.setGrammarConcept(lr.get(GRAMMAR_LESSON.GRAMMAR_CONCEPT));
                            lesson.setExplanation(lr.get(GRAMMAR_LESSON.EXPLANATION));

                            Result<Record> sentenceRecords = lr.get("exampleLessonSentences", Result.class);
                            List<LessonSentence> sentences = new ArrayList<>();
                            for (Record sr : sentenceRecords) {
                                LessonSentence sentence = new LessonSentence();
                                sentence.setId(sr.get(SENTENCE.ID));
                                sentence.setText(sr.get(SENTENCE.TEXT));
                                sentence.setTranslation(sr.get(SENTENCE.TRANSLATION));
                                lesson.addExampleSentence(sentence);
                                sentences.add(sentence);
                            }
                            page.setLesson(lesson);
                            lesson.setLessonPage(page);
                        }
                    }
                    case CONJUGATION -> {
                        Result<Record> lessonRecords = pr.get("conjugationLesson", Result.class);
                        if (!lessonRecords.isEmpty()) {
                            Record lr = lessonRecords.getFirst();
                            ConjugationLesson lesson = new ConjugationLesson();
                            lesson.setId(lr.get(CONJUGATION_LESSON.ID));
                            lesson.setType(lessonType);
                            lesson.setTitle(lessonTitle);
                            lesson.setExplanation(lr.get(CONJUGATION_LESSON.EXPLANATION));
                            lesson.setConjugationRuleName(lr.get(CONJUGATION_LESSON.CONJUGATION_RULE_NAME));

                            Result<Record> conjugatedWordRecords = lr.get("conjugatedWords", Result.class);
                            List<LessonConjugationExample> conjugationExamples = new ArrayList<>();
                            for (Record cwr : conjugatedWordRecords) {
                                LessonConjugationExample conjugationExample = new LessonConjugationExample();
                                conjugationExample.setId(cwr.get(CONJUGATION_EXAMPLE.ID));
                                conjugationExample.setConjugatedForm(cwr.get(CONJUGATION_EXAMPLE.CONJUGATED_FORM));
                                conjugationExample.setExampleSentence(cwr.get(CONJUGATION_EXAMPLE.EXAMPLE_SENTENCE));
                                conjugationExample.setInfinitive(cwr.get(CONJUGATION_EXAMPLE.INFINITIVE));
                                conjugationExample.setSentenceTranslation(cwr.get(CONJUGATION_EXAMPLE.SENTENCE_TRANSLATION));
                                conjugationExamples.add(conjugationExample);
                            }
                            lesson.setConjugatedWords(conjugationExamples);
                            page.setLesson(lesson);
                            lesson.setLessonPage(page);
                        }
                    }
                    case PRACTICE -> {
                        Result<Record> lessonRecords = pr.get("practiceLesson", Result.class);
                        if (!lessonRecords.isEmpty()) {
                            Record lr = lessonRecords.getFirst();
                            PracticeLesson lesson = new PracticeLesson();
                            lesson.setId(lr.get(PRACTICE_LESSON.ID));
                            lesson.setType(lessonType);
                            lesson.setTitle(lessonTitle);
                            lesson.setInstructions(lr.get(PRACTICE_LESSON.INSTRUCTIONS));

                            Result<Record> questionRecords = lr.get("lessonQuestions", Result.class);
                            List<LessonQuestion> questions = new ArrayList<>();
                            for (Record qr : questionRecords) {
                                LessonQuestion question = new LessonQuestion();
                                question.setId(qr.get(LESSON_QUESTION.ID));
                                question.setQuestionType(QuestionType.FREE_FORM);
                                question.setQuestionText(qr.get(LESSON_QUESTION.QUESTION_TEXT));
                                lesson.addQuestion(question);
                                questions.add(question);
                            }
                            page.setLesson(lesson);
                            lesson.setLessonPage(page);
                        }
                    }
                    case READING_COMPREHENSION -> {
                        Result<Record> lessonRecords = pr.get("readingComprehensionLesson", Result.class);
                        if (!lessonRecords.isEmpty()) {
                            Record lr = lessonRecords.getFirst();
                            ReadingComprehensionLesson lesson = new ReadingComprehensionLesson();
                            lesson.setId(lr.get(READING_COMPREHENSION_LESSON.ID));
                            lesson.setType(lessonType);
                            lesson.setTitle(lessonTitle);
                            lesson.setStory(lr.get(READING_COMPREHENSION_LESSON.STORY));

                            Result<Record> questionRecords = lr.get("lessonQuestions", Result.class);
                            List<LessonQuestion> questions = new ArrayList<>();
                            for (Record qr : questionRecords) {
                                LessonQuestion question = new LessonQuestion();
                                question.setId(qr.get(LESSON_QUESTION.ID));
                                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                                question.setQuestionText(qr.get(LESSON_QUESTION.QUESTION_TEXT));
                                question.setAnswer(qr.get(LESSON_QUESTION.ANSWER));

                                String[] answerChoices = qr.get("answerChoices", String[].class);
                                question.setAnswerChoices(answerChoices != null ? List.of(answerChoices) : new ArrayList<>());
                                lesson.addQuestion(question);
                                questions.add(question);
                            }
                            page.setLesson(lesson);
                            lesson.setLessonPage(page);
                        }
                    }
                }
                pages.add(page);
            }
            chapter.setLessonPages(pages);
            chapters.add(chapter);
        }

        lessonBook.setLessonChapters(chapters);
        return lessonBook;
    }

    private SelectJoinStep<Record> selectLessonBookDetails() {
        return dsl.select(
                LESSON_BOOK.asterisk(),
                multiset(
                        select(
                                LESSON_CHAPTER.asterisk(),
                                multiset(
                                        select(
                                                LESSON_PAGE.asterisk(),
                                                LESSON.TYPE,
                                                LESSON.TITLE,
                                                multiset(
                                                        select(
                                                                VOCABULARY_LESSON.asterisk(),
                                                                multiset(
                                                                        select(
                                                                                WORD.asterisk()
                                                                        ).from(WORD)
                                                                                .join(VOCABULARY_LESSON_WORD).on(VOCABULARY_LESSON_WORD.WORD_ID.eq(WORD.ID))
                                                                                .where(VOCABULARY_LESSON_WORD.LESSON_ID.eq(VOCABULARY_LESSON.ID))
                                                                ).as("vocabularies")
                                                        ).from(VOCABULARY_LESSON)
                                                                .where(VOCABULARY_LESSON.ID.eq(LESSON_PAGE.LESSON_ID))
                                                ).as("vocabularyLesson"),
                                                multiset(
                                                        select(
                                                                GRAMMAR_LESSON.asterisk(),
                                                                multiset(
                                                                        select(
                                                                                SENTENCE.asterisk()
                                                                        ).from(SENTENCE)
                                                                                .join(GRAMMAR_LESSON_SENTENCE).on(GRAMMAR_LESSON_SENTENCE.SENTENCE_ID.eq(SENTENCE.ID))
                                                                                .where(GRAMMAR_LESSON_SENTENCE.LESSON_ID.eq(GRAMMAR_LESSON.ID))
                                                                ).as("exampleLessonSentences")
                                                        ).from(GRAMMAR_LESSON)
                                                                .where(GRAMMAR_LESSON.ID.eq(LESSON_PAGE.LESSON_ID))
                                                ).as("grammarLesson"),
                                                multiset(
                                                        select(
                                                                CONJUGATION_LESSON.asterisk(),
                                                                multiset(
                                                                        select(
                                                                                CONJUGATION_EXAMPLE.asterisk()
                                                                        ).from(CONJUGATION_EXAMPLE)
                                                                                .join(CONJUGATION_LESSON_EXAMPLE).on(CONJUGATION_LESSON_EXAMPLE.EXAMPLE_ID.eq(CONJUGATION_EXAMPLE.ID))
                                                                                .where(CONJUGATION_LESSON_EXAMPLE.LESSON_ID.eq(CONJUGATION_LESSON.ID))
                                                                ).as("conjugatedWords")
                                                        ).from(CONJUGATION_LESSON)
                                                                .where(CONJUGATION_LESSON.ID.eq(LESSON_PAGE.LESSON_ID))
                                                ).as("conjugationLesson"),
                                                multiset(
                                                        select(
                                                                PRACTICE_LESSON.asterisk(),
                                                                multiset(
                                                                        select(
                                                                                LESSON_QUESTION.asterisk()
                                                                        ).from(LESSON_QUESTION)
                                                                                .where(LESSON_QUESTION.LESSON_ID.eq(PRACTICE_LESSON.ID))
                                                                ).as("lessonQuestions")
                                                        ).from(PRACTICE_LESSON)
                                                                .where(PRACTICE_LESSON.ID.eq(LESSON_PAGE.LESSON_ID))
                                                ).as("practiceLesson"),
                                                multiset(
                                                        select(
                                                                READING_COMPREHENSION_LESSON.asterisk(),
                                                                multiset(
                                                                        select(
                                                                                LESSON_QUESTION.asterisk(),
                                                                                field(
                                                                                        select(arrayAgg(QUESTION_ANSWER_CHOICE.ANSWER_CHOICE))
                                                                                                .from(QUESTION_ANSWER_CHOICE)
                                                                                                .where(QUESTION_ANSWER_CHOICE.QUESTION_ID.eq(LESSON_QUESTION.ID))
                                                                                ).as("answerChoices")
                                                                        ).from(LESSON_QUESTION)
                                                                                .where(LESSON_QUESTION.LESSON_ID.eq(READING_COMPREHENSION_LESSON.ID))
                                                                ).as("lessonQuestions")
                                                        ).from(READING_COMPREHENSION_LESSON)
                                                                .where(READING_COMPREHENSION_LESSON.ID.eq(LESSON_PAGE.LESSON_ID))
                                                ).as("readingComprehensionLesson")
                                        ).from(LESSON_PAGE)
                                                .join(LESSON).on(LESSON_PAGE.LESSON_ID.eq(LESSON.ID))
                                                .where(LESSON_PAGE.LESSON_CHAPTER_ID.eq(LESSON_CHAPTER.ID))
                                                .orderBy(LESSON_PAGE.ID.asc())
                                ).as("lessonPages")
                        ).from(LESSON_CHAPTER)
                                .where(LESSON_CHAPTER.BOOK_ID.eq(LESSON_BOOK.ID))
                                .orderBy(LESSON_CHAPTER.ID.asc())
                ).as("lessonChapters")
        ).from(LESSON_BOOK);
    }

    private void deleteGrandchildren(
        CommonTableExpression<?> chapters,
        CommonTableExpression<?> pages,
        CommonTableExpression<?> lessons,
        CommonTableExpression<?> questions
    ) {
        dsl.deleteFrom(QUESTION_ANSWER_CHOICE)
                .where(QUESTION_ANSWER_CHOICE.QUESTION_ID.in(
                    dsl.with(chapters).with(pages).with(lessons).with(questions).select(
                        field("id", Long.class)
                    ).from(questions)
                )).execute();

        dsl.deleteFrom(GRAMMAR_LESSON_SENTENCE)
                .where(GRAMMAR_LESSON_SENTENCE.LESSON_ID.in(
                    dsl.with(chapters).with(pages).with(lessons).select(
                        field("lesson_id", Long.class)
                    ).from(lessons)
                )).execute();

        List<Long> conjugationExampleIds = dsl.with(chapters).with(pages).with(lessons)
                .select(CONJUGATION_LESSON_EXAMPLE.EXAMPLE_ID)
                .from(CONJUGATION_LESSON_EXAMPLE)
                .where(CONJUGATION_LESSON_EXAMPLE.LESSON_ID.in(
                        select(field("lesson_id", Long.class)).from(lessons)
                ))
                .fetchInto(Long.class);

        if (!conjugationExampleIds.isEmpty()) {
            dsl.deleteFrom(CONJUGATION_LESSON_EXAMPLE)
                    .where(CONJUGATION_LESSON_EXAMPLE.EXAMPLE_ID.in(conjugationExampleIds))
                    .execute();

            dsl.deleteFrom(CONJUGATION_EXAMPLE)
                    .where(CONJUGATION_EXAMPLE.ID.in(conjugationExampleIds))
                    .execute();
        }

        dsl.deleteFrom(VOCABULARY_LESSON_WORD)
                .where(VOCABULARY_LESSON_WORD.LESSON_ID.in(
                    dsl.with(chapters).with(pages).with(lessons).select(
                        field("lesson_id", Long.class)
                    ).from(lessons)
                )).execute();

        dsl.deleteFrom(LESSON_QUESTION)
                .where(LESSON_QUESTION.LESSON_ID.in(
                    dsl.with(chapters).with(pages).with(lessons).select(
                        field("lesson_id", Long.class)
                    ).from(lessons)
                )).execute();
    }

    private void deleteChildren(
            Long lessonBookId,
            CommonTableExpression<?> chapters,
            CommonTableExpression<?> pages,
            CommonTableExpression<?> lessons
    ) {
        var lessonIds = dsl.with(chapters).with(pages).with(lessons).select(
            field("lesson_id", Long.class)
        ).from(lessons);

        dsl.deleteFrom(VOCABULARY_LESSON).where(VOCABULARY_LESSON.ID.in(lessonIds)).execute();
        dsl.deleteFrom(GRAMMAR_LESSON).where(GRAMMAR_LESSON.ID.in(lessonIds)).execute();
        dsl.deleteFrom(CONJUGATION_LESSON).where(CONJUGATION_LESSON.ID.in(lessonIds)).execute();
        dsl.deleteFrom(PRACTICE_LESSON).where(PRACTICE_LESSON.ID.in(lessonIds)).execute();
        dsl.deleteFrom(READING_COMPREHENSION_LESSON).where(READING_COMPREHENSION_LESSON.ID.in(lessonIds)).execute();

        dsl.deleteFrom(LESSON_PAGE)
                .where(LESSON_PAGE.LESSON_CHAPTER_ID.in(dsl.with(chapters).select(
                        field("id", Long.class)
                    ).from(chapters)
                )).execute();

        dsl.deleteFrom(LESSON)
                .where(LESSON.ID.in(dsl.with(chapters).with(pages).select(
                        field("lesson_id", Long.class)
                    ).from(pages)
                )).execute();

        // 6. Delete chapters
        dsl.deleteFrom(LESSON_CHAPTER)
                .where(LESSON_CHAPTER.BOOK_ID.eq(lessonBookId))
                .execute();
    }
}
