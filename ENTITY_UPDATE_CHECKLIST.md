# Entity Property Update Checklist

This document provides a comprehensive checklist for developers when adding, removing, or updating a property on a database entity. Following this checklist ensures that the change is propagated correctly through all layers of the application, from the database to the frontend UI.

## Example Scenario

We will use the example of adding a `words_to_highlight` field to the `StoryParagraph` entity.

---

## 1. Backend Changes

### 1.1. Database Layer

The database schema is the source of truth.

- [ ] **Update Schema Definition:** Modify the `CREATE TABLE` statement in `src/main/resources/db/migration/dev-schema.sql`.
  - **Example:** Added `words_to_highlight jsonb not null default '[]'::jsonb` to the `story_paragraphs` table.
  - **Note:** Ensure constraints like `NOT NULL` and `default` values are considered.

### 1.2. Persistence Layer

This layer maps database tables to Java objects.

- [ ] **Update JPA Entity:** Modify the `@Entity` class to reflect the schema change.
  - **File:** `src/main/java/com/example/language_learning/.../YourEntity.java`
  - **Example:** Added `private Set<String> wordsToHighlight` to `StoryParagraph.java`.
  - **Annotations:** Ensure JPA (`@Column`) and Hibernate (`@JdbcTypeCode(SqlTypes.JSON)`) annotations are correct. Match nullability constraints.

- [ ] **Run jOOQ Code Generator:** After changing the database schema, you **must** regenerate the jOOQ classes.
  - **Action:** Run the `./gradlew generateJooq` Gradle task.
  - **Result:** This updates the classes in `build/generated-sources/jooq`, which are used by custom repositories.

- [ ] **Update Custom jOOQ Repositories:** Check all custom repository implementations (`...RepositoryImpl.java`) for hardcoded queries that need to be updated.
  - **Write Path:** Check `INSERT` or `UPDATE` queries. Ensure the new field is being set on the jOOQ `Record` object before insertion.
    - **Example:** In `StoryPageRepositoryImpl.java`, we updated the creation of `StoryParagraphRecord` to serialize and set the `wordsToHighlight` field.
  - **Read Path:** Check `SELECT` queries. Ensure the new column is included in the `select` list and that the manual mapping logic correctly deserializes the value and sets it on the entity.
    - **Example:** In `StoryBookRepositoryImpl.java`, we added `STORY_PARAGRAPH.WORDS_TO_HIGHLIGHT` to the `select` and added deserialization logic using `ObjectMapper`.

### 1.3. API Layer

This layer defines the contract between the backend and the frontend.

- [ ] **Update DTO:** Modify the Data Transfer Object (`...DTO.java`) that corresponds to the entity.
  - **File:** `src/main/java/com/example/language_learning/.../YourEntityDTO.java`
  - **Example:** Added `Set<String> wordsToHighlight` to `StoryParagraphDTO.java`.

- [ ] **Verify DTO Mapper:** Check the MapStruct interface (`...StructMapper.java`).
  - **Note:** In most cases, if the field names are identical between the Entity and the DTO, **no changes are needed.** MapStruct handles it automatically.

- [ ] **Update GraphQL Schema:** Modify the `.graphqls` schema to expose the new field to the frontend.
  - **File:** `src/main/resources/graphql/....graphqls`
  - **Example:** Added `wordsToHighlight: [String!]!` to the `StoryParagraph` type in `storybook.graphqls`.
  - **Note:** Ensure the nullability (`!`) matches the DTO and database constraints.

### 1.4. Business Logic Layer

This is where the data for the new field is actually generated.

- [ ] **Update Service Logic:** Modify the service class (`...Service.java`) that is responsible for creating or updating the entity.
  - **File:** `src/main/java/com/example/language_learning/.../YourService.java`
  - **Example:** In `StoryPageService.java`, we added the logic to analyze paragraph content, find the highlightable words, and call `paragraph.setWordsToHighlight(...)` before saving.

---

## 2. Frontend Changes

### 2.1. Data Layer

This layer defines the types and queries for fetching data from the backend.

- [ ] **Update TypeScript DTO:** Modify the TypeScript interface that defines the shape of the data received from the API.
  - **File:** `frontend/src/shared/types/dto.ts`
  - **Example:** Added `wordsToHighlight: string[]` to the `StoryParagraphDTO` interface.

- [ ] **Update GraphQL Query/Fragment:** Ensure the frontend is requesting the new field. This is often done in a fragment.
  - **File:** `frontend/src/.../fragments.ts`
  - **Example:** Added `wordsToHighlight` to the `storyParagraphFragment` in `fragments.ts`.

### 2.2. UI/Component Layer

This layer uses the new data to render something for the user.

- [ ] **Update Component Logic:** Modify the React component that consumes the data.
  - **File:** `frontend/src/features/.../YourComponent.tsx`
  - **Example:** In `StoryContentPage.tsx`, we replaced the old highlighting logic with a new, efficient regex-based approach that uses the `wordsToHighlight` array.
