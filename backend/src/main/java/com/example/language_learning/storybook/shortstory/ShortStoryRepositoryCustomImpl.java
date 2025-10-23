package com.example.language_learning.storybook.shortstory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.language_learning.generated.jooq.tables.ShortStory.SHORT_STORY;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ShortStoryRepositoryCustomImpl implements ShortStoryRepositoryCustom{
    private final DSLContext dsl;

    @Override
    public void deleteStoryById(Long storyId) {
        dsl.deleteFrom(SHORT_STORY)
                .where(SHORT_STORY.ID.eq(storyId))
                .execute();
        log.info("Deleted story with ID: {}", storyId);
    }
}
