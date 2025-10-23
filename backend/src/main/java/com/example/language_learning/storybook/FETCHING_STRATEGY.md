# Fetching Strategy for Complex Nested Collections

This document outlines the current best-practice strategy used to fetch the `StoryBook` entity graph, which includes multiple nested `List` collections (`StoryBook` -> `ShortStory` -> `StoryPage` -> `StoryParagraph` / `StoryVocabularyItem`).

The previous JPA-based hybrid approach ultimately failed. It proved to be overly complex, difficult to maintain, and required multiple database queries and manual data-stitching in the service layer. This has been replaced by a single, efficient jOOQ query.

---

## The jOOQ `multiset` Strategy

To overcome the complexity and inefficiency of the multi-query JPA approach, we have adopted a modern strategy using jOOQ's `multiset` operator. This allows us to fetch the entire nested object graph in a **single, highly efficient database query**.

### The Concept: Pushing Aggregation to the Database

Instead of fetching flat tabular data and stitching it together in Java, we instruct the database (PostgreSQL) to build the nested JSON structure for us directly. jOOQ then transparently maps this JSON structure into our Java objects.

This completely eliminates the `MultipleBagFetchException` and the N+1 query problem in one step.

### The jOOQ Implementation

The repository method uses `multiset` to define the nested collections. The query is complex, but this complexity is now properly contained within the data access layer.

**File: `StoryBookRepositoryImpl.java`**
```java
@Override
public Optional<StoryBook> findStoryBookDetailsById(Long id) {
    return dsl.select(
                    STORY_BOOKS.ID, STORY_BOOKS.TITLE, STORY_BOOKS.DIFFICULTY, STORY_BOOKS.LANGUAGE,
                    multiset(
                        dsl.select(
                                SHORT_STORIES.ID, SHORT_STORIES.CHAPTER_NUMBER, SHORT_STORIES.TITLE, SHORT_STORIES.NATIVE_TITLE, SHORT_STORIES.GENRE, SHORT_STORIES.TOPIC,
                                multiset(
                                    dsl.select(
                                        STORY_PAGES.ID, STORY_PAGES.PAGE_NUMBER, STORY_PAGES.TYPE, STORY_PAGES.IMAGE_URL, STORY_PAGES.ENGLISH_SUMMARY,
                                        multiset(dsl.select(STORY_PARAGRAPHS.ID, STORY_PARAGRAPHS.PARAGRAPH_NUMBER, cast(STORY_PARAGRAPHS.CONTENT, String.class).as("content")).from(STORY_PARAGRAPHS).where(STORY_PARAGRAPHS.STORY_PAGE_ID.eq(STORY_PAGES.ID))).as("paragraphs"),
                                        multiset(dsl.select(STORY_VOCABULARY_ITEMS.ID, STORY_VOCABULARY_ITEMS.WORD, STORY_VOCABULARY_ITEMS.TRANSLATION, STORY_VOCABULARY_ITEMS.PAGE_NUMBER).from(STORY_VOCABULARY_ITEMS).where(STORY_VOCABULARY_ITEMS.STORY_PAGE_ID.eq(STORY_PAGES.ID))).as("vocabulary")
                                    ).from(STORY_PAGES).where(STORY_PAGES.SHORT_STORY_ID.eq(SHORT_STORIES.ID))
                                ).as("storyPages")
                        ).from(SHORT_STORIES).where(SHORT_STORIES.STORY_BOOK_ID.eq(STORY_BOOKS.ID))
                    ).as("shortStories")
            )
            .from(STORY_BOOKS)
            .where(STORY_BOOKS.ID.eq(id))
            .fetchOptional(r -> { /* Manual mapping logic... */ });
}
```

### The Resulting SQL Query

jOOQ translates the Java code above into a single, powerful SQL query using PostgreSQL's native JSON functions. This is what is actually sent to the database.

```sql
SELECT
    sb.id, sb.title, sb.difficulty, sb.language,
    (
        SELECT json_agg(json_build_object(
            'id', ss.id,
            'chapterNumber', ss.chapter_number,
            'title', ss.title,
            'nativeTitle', ss.native_title,
            'genre', ss.genre,
            'topic', ss.topic,
            'storyPages', (
                SELECT json_agg(json_build_object(
                    'id', sp.id,
                    'pageNumber', sp.page_number,
                    'type', sp.type,
                    'imageUrl', sp.image_url,
                    'englishSummary', sp.english_summary,
                    'paragraphs', (
                        SELECT json_agg(json_build_object('id', p.id, 'content', CAST(p.content AS TEXT)))
                        FROM story_paragraphs p WHERE p.story_page_id = sp.id
                    ),
                    'vocabulary', (
                        SELECT json_agg(json_build_object('id', v.id, 'word', v.word))
                        FROM story_vocabulary_items v WHERE v.story_page_id = sp.id
                    )
                ))
                FROM story_pages sp WHERE sp.short_story_id = ss.id
            )
        ))
        FROM short_stories ss WHERE ss.story_book_id = sb.id
    ) AS short_stories
FROM
    story_books sb
WHERE
    sb.id = ?;
```

### Simplified Service Layer

With the jOOQ strategy, the service layer becomes dramatically simpler. All the complex data-stitching logic is gone, replaced by a single, clear call to the repository.

**File: `StoryBookService.java` (Current Implementation)**
```java
@Transactional(readOnly = true)
public StoryBook getStoryBookDetails(Long id) {
    return storyBookRepository.findStoryBookDetailsById(id)
            .orElseThrow(() -> new RuntimeException("Storybook with id '" + id + "' not found"));
}
```

This new approach is more efficient, easier to maintain, and less prone to bugs.
