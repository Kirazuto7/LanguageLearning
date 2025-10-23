package com.example.language_learning.user.dashboard;

import com.example.language_learning.user.dashboard.library.LessonBookLibraryItem;
import com.example.language_learning.user.dashboard.library.StoryBookLibraryItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.language_learning.generated.jooq.Tables.*;
import static com.example.language_learning.generated.jooq.tables.LessonBook.LESSON_BOOK;
import static com.example.language_learning.generated.jooq.tables.StoryBook.STORY_BOOK;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class UserDataRepositoryImpl implements UserDataRepository {

    private final DSLContext dsl;
    private static final int LIBRARY_ITEM_LIMIT = 10;
    private static final String LESSON_BOOK_TYPE = "LESSON_BOOK";
    private static final String STORY_BOOK_TYPE = "STORY_BOOK";

    @Override
    public UserDataDTO findUserDataByUserId(Long userId) {
        Field<Integer> chapterCount = select(count())
                .from(LESSON_CHAPTER)
                .where(LESSON_CHAPTER.BOOK_ID.eq(LESSON_BOOK.ID))
                .asField("chapterCount");

        Field<Integer> lessonPageCount = select(count())
                .from(LESSON_PAGE)
                .join(LESSON_CHAPTER).on(LESSON_PAGE.LESSON_CHAPTER_ID.eq(LESSON_CHAPTER.ID))
                .where(LESSON_CHAPTER.BOOK_ID.eq(LESSON_BOOK.ID))
                .asField("pageCount");

        List<LessonBookLibraryItem> lessonBooks = dsl.select(
            LESSON_BOOK.ID,
            LESSON_BOOK.TITLE,
            LESSON_BOOK.LANGUAGE,
            LESSON_BOOK.DIFFICULTY,
            LESSON_BOOK.CREATED_AT,
            DSL.val(LESSON_BOOK_TYPE).as("type"),
            chapterCount,
            lessonPageCount
        )
                .from(LESSON_BOOK)
                .where(LESSON_BOOK.USER_ID.eq(userId))
                .orderBy(LESSON_BOOK.CREATED_AT.desc())
                .limit(LIBRARY_ITEM_LIMIT)
                .fetchInto(LessonBookLibraryItem.class);

        Field<Integer> storyCount = select(count())
                .from(SHORT_STORY)
                .where(SHORT_STORY.STORY_BOOK_ID.eq(STORY_BOOK.ID))
                .asField("storyCount");

        Field<Integer> storyPageCount = select(count())
                .from(STORY_PAGE)
                .join(SHORT_STORY).on(STORY_PAGE.SHORT_STORY_ID.eq(SHORT_STORY.ID))
                .where(SHORT_STORY.STORY_BOOK_ID.eq(STORY_BOOK.ID))
                .asField("pageCount");

        List<StoryBookLibraryItem> storyBooks = dsl.select(
            STORY_BOOK.ID,
            STORY_BOOK.TITLE,
            STORY_BOOK.LANGUAGE,
            STORY_BOOK.DIFFICULTY,
            STORY_BOOK.CREATED_AT,
            DSL.val(STORY_BOOK_TYPE).as("type"),
            storyCount,
            storyPageCount
        )
                .from(STORY_BOOK)
                .where(STORY_BOOK.USER_ID.eq(userId))
                .orderBy(STORY_BOOK.CREATED_AT.desc())
                .limit(LIBRARY_ITEM_LIMIT)
                .fetchInto(StoryBookLibraryItem.class);

        return UserDataDTO.builder()
                .lessonBooks(lessonBooks)
                .storyBooks(storyBooks)
                .build();
    }
}
