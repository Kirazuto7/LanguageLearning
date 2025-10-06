package com.example.language_learning.user.dashboard;

import com.example.language_learning.user.dashboard.library.LessonBookLibraryItem;
import com.example.language_learning.user.dashboard.library.LibraryItem;
import com.example.language_learning.user.dashboard.library.StoryBookLibraryItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.language_learning.generated.jooq.tables.LessonBook.LESSON_BOOK;
import static com.example.language_learning.generated.jooq.tables.StoryBook.STORY_BOOK;

@Repository
@RequiredArgsConstructor
public class UserDataRepositoryImpl implements UserDataRepository {

    private final DSLContext dsl;
    private static final int LIBRARY_ITEM_LIMIT = 10;

    @Override
    public UserDataDTO findUserDataByUserId(Long userId) {
        List<LessonBookLibraryItem> lessonBooks = dsl.selectFrom(LESSON_BOOK)
                .where(LESSON_BOOK.USER_ID.eq(userId))
                .orderBy(LESSON_BOOK.CREATED_AT.desc())
                .limit(LIBRARY_ITEM_LIMIT)
                .fetchInto(LessonBookLibraryItem.class);

        List<StoryBookLibraryItem> storyBooks = dsl.selectFrom(STORY_BOOK)
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
