# Storybook Feature Specification

This document outlines the design and business logic for the AI-powered storybook generation feature.

---

## User Story & Task Checklist

As a user, I want to generate a complete, illustrated short story based on a genre and an optional topic, so that I can have new, engaging reading material.

### Backend Implementation Status

-   [x] **Core Workflow:** Define the asynchronous, multi-state generation process (`INITIAL`, `METADATA`, `STORY_GENERATION`, `IMAGE_GENERATION`, `COMPLETED`).
-   [x] **Synchronous Setup:** Implement the `StoryPrepWorkflow` to create the initial `ShortStory` shell entity before the async job.
-   [x] **State Machine Actions:** Implement the `StoryGenerationActions` to handle the logic for each state.
-   [x] **Recursive Persistence:** Implement the `PERSIST_PAGES` state to be recursive, processing one page at a time.
-   [x] **Prompt Engineering:** Create robust instruction prompts for `STORY_METADATA` and `STORY_PAGES`. Image prompts are now dynamically generated.
-   [x] **Schema Definition:** Create language-specific JSON schemas to validate the AI's output for all prompt types.
-   [x] **Configuration:** Update `PromptType` enum and `AIConfig` to correctly load all new `storybook` prompts and schemas.
-   [x] **Storage Abstraction:** Define a generic `StorageProvider` interface and `StorageProperties` interface.
-   [x] **Development Storage:** Implement a `MinioStorageProvider` and configure Docker and `application.yml` for local MinIO.
-   [x] **Image Service:** Create a dedicated `ImageService` to handle saving Base64-encoded images to storage.
-   [x] **Data Mapping:** Implement a context-aware `AIStoryMapper` to transform raw AI DTOs into application DTOs.
-   [x] **Mapper Registration:** Register all new `storybook` mappers in the `AIResponseMapperRegistry`.
-   [x] **Image Client:** Implement a custom `StableDiffusionClient` and wrap it in a Spring AI `ImageModel` for flexible integration.
-   [x] **AIEngine Refactor:** Refactor the `AIEngine` to support both `ChatClient` (for text) and `ImageModel` (for images) via `generate()` and `generateImages()` methods.
-   [ ] **Image Alt Text:** Add a new `imageAltText` field to the `StoryPage` entity and DTOs to store the `revised_prompt` for SEO and accessibility.

---

## 1. Core Architectural Model

-   **`Storybook`:** A `Storybook` acts as a collection or anthology of short stories. Each user has a unique `Storybook` for each `(language, difficulty)` combination (e.g., "Beginner Korean Storybook"). It does not have a genre itself.

-   **`ShortStory`:** A `ShortStory` is the core generated entity. Each `ShortStory` has its own `title`, `genre`/`topic`, and a list of `StoryPages`. It is contained within a parent `Storybook`.

-   **`StoryPage`:** A page within a `ShortStory`. It can be a content page with paragraphs or a vocabulary page. It contains an `imageUrl` field to store the path to its illustration and an `englishSummary` to be used as a prompt for image generation.
    -   The type of page is explicitly defined by a `StoryPageType` enum (`CONTENT` or `VOCABULARY`).

-   **`StoryVocabularyItem`:** A single vocabulary word. It includes a `pageNumber` field that references the book-relative page number where the word first appeared, providing context for review.

---

## 2. Paragraph & Page Definitions

### 2.1. Paragraph
A single **Paragraph** is defined as a block of text containing **3 to 5 sentences**.

### 2.2. Page Content by Difficulty

-   **Beginner Pages:** 1 Paragraph per page.
-   **Intermediate Pages:** 2 Paragraphs per page.
-   **Advanced Pages:** 3 Paragraphs per page.

---

## 3. Generation Workflow (Revised)

The end-to-end generation of a new `ShortStory` is an asynchronous, multi-state process managed by a state machine.

1.  **Initiation (Synchronous):**
    a.  The user provides a **Genre** and an optional **Topic**.
    b.  A synchronous workflow creates the initial `ShortStory` entity with placeholder titles and saves it.
    c.  An asynchronous job is submitted to the `JobQueueService`.

2.  **State 1: Metadata Generation (`STORY_METADATA`)**
    a.  The state machine's first action calls the AI with a prompt to generate the story's `title`, `nativeTitle`, and a final `topic` (either the user's or one invented by the AI).
    b.  The `ShortStory` entity is updated with this metadata and saved.

3.  **State 2: Page Content Generation (`STORY_GENERATION`)**
    a.  The next action calls the AI with a single, large prompt containing the story's metadata.
    b.  The AI returns a complete JSON object containing a list of all pages for the story, including the `englishSummary`, `content` (with paragraphs separated by `\n`), and `vocabulary` for each page.
    c.  The `AIStoryMapper` processes this response:
        -   It de-duplicates vocabulary across all pages to ensure each word is introduced only once.
        -   It creates a `StoryContentPageDTO` for each page from the AI, which includes a small, unique list of vocabulary for that page.
        -   It aggregates all unique vocabulary into a master list and creates a final `StoryVocabularyPageDTO` which is appended to the list of pages.
    d.  This complete list of page DTOs (content pages + final vocab page) is held in memory and passed to the next state. **Nothing is persisted to the database in this step.**

4.  **State 3: Image Generation (`IMAGE_GENERATION`)**
    a.  This state is **not recursive**. It processes all pages at once.
    b.  It extracts the `englishSummary` from every page DTO and sends them as a batch of prompts to the `AIEngine.generateImages()` method.
    c.  The engine generates all images concurrently and returns a `Map` linking each original prompt to its permanent image URL.
    d.  The action then updates the list of page DTOs in memory, assigning the correct URL to each page by looking it up in the map.
    e.  The updated list of DTOs is passed to the next state. **Nothing is persisted to the database in this step.**

5.  **State 4: Recursive Page Persistence (`PERSIST_PAGES`)**
    a.  This state is **recursive**. It processes one page DTO from the list at a time.
    b.  For the current `CONTENT` page DTO being processed:
        -   It gets the final, atomic `pageNumber` for the book.
        -   It updates the `pageNumber` for the vocabulary items on *that specific page*.
        -   It also updates the corresponding items in the master vocabulary list on the final page DTO held in memory.
    c.  It then converts the DTO to a `StoryPage` entity, links it to the parent `ShortStory`, and **persists it to the database**.
    d.  The state machine then transitions back to itself, incrementing an index to process the next page in the list.

6.  **State 5: Completion (`COMPLETED`)**
    a.  Once all pages have been processed, the state machine transitions to the terminal `COMPLETED` state, and the process ends.

---

## 4. Image Storage Strategy

To handle image storage in a scalable and professional manner, the application will not store image files directly in the database or project directory.

### 4.1. Development Environment

-   **Status:** Implemented.
-   **Technology:** A **MinIO** Docker container will be used. MinIO is an S3-compatible object storage server that runs locally.
-   **Workflow:** The backend's `ImageStorageService` will be configured to point to the local MinIO container.
-   **Data Synchronization:** To ensure a clean state and prevent orphaned images, the MinIO storage volume will be destroyed along with the database volume when running `docker-compose down -v`. This provides a perfectly ephemeral development environment.

### 4.2. Production Environment (Future)

-   **Technology:** A cloud-based object storage service like **AWS S3** or **Cloudinary** will be used.
-   **Workflow:** The `ImageStorageService` implementation will be switched to use the production provider's SDK and credentials. No application code changes will be required.
-   **Lifecycle Management:** A cleanup mechanism (e.g., using JPA event listeners or a scheduled job) will be implemented to delete images from cloud storage when their corresponding `StoryPage` is deleted from the database.

---

## 5. Prompt Examples & Test Results

This section contains successful AI responses from the design and testing phase.

### 5.1. User-Provided Topic Example (Combined Content & Vocabulary)

This test proves that the AI can generate both story content and relevant vocabulary in a single, structured JSON response.

#### Prompt
```
You are a creative author and language assistant writing a simple story for a beginner language learner.

The story is about "a magical tiger that can talk."

Your task is to write the content for the first 4 pages of this story. The story must be written in **Korean**.

You MUST provide the output in a valid JSON format. The root object should be a `pages` array containing **exactly 4 objects**.

Each object in the array represents a single page and must have the following properties:
- `content`: A single string containing approximately 3 to 5 simple sentences for that specific page, written in Korean.
- `vocabulary`: An array of 2 to 3 key vocabulary words extracted from that page's `content`. Each object in the `vocabulary` array must have:
    - `word`: The Korean word.
    - `translation`: The English translation.

Do not include any other text, notes, or explanations in your response. Provide only the raw JSON.
```

#### Result
```json
{
  "pages": [
    {
      "content": "옛날 옛적에 숲 속에 마법의 호랑이 티코가 살고 있었어요. 아이들은 티코를 만나 이야기를 듣기 위해 자주 숲에 갔어요. 티코는 아이들에게 항상 친절하게 대해주었어요.",
      "vocabulary": [
        {
          "word": "호랑이",
          "translation": "Tiger"
        },
        {
          "word": "친절하게",
          "translation": "kindly"
        },
        {
          "word": "자주",
          "translation": "often"
        }
      ]
    },
    {
      "content": "어느 날, 민지가 숲에서 길을 잃었어요. 그때 마법의 호랑이 티코가 나타나 민지에게 길을 안내해주었어요. \"어디로 가야 하나요?\" 티코가 물어봤어요.",
      "vocabulary": [
        {
          "word": "길을 잃다",
          "translation": "get lost"
        },
        {
          "word": "나타나",
          "translation": "appear"
        },
        {
          "word": "물어봤어요",
          "translation": "asked"
        }
      ]
    },
    {
      "content": "티코와 민지는 함께 숲을 거닐며 이야기를 나눴어요. 티코는 민지에게 동물 친구들에 대한 이야기를 들려주었어요. 민지는 행복하게 듣고 있었어요.",
      "vocabulary": [
        {
          "word": "거닐다",
          "translation": "walk around"
        },
        {
          "word": "들려주었어요",
          "translation": "told"
        },
        {
          "word": "행복하게",
          "translation": "happily"
        }
      ]
    },
    {
      "content": "그날 이후 민지는 티코를 친구로 생각하게 되었어요. 다음에 숲에 가면 티코를 다시 만날 수 있기를 바랐어요.",
      "vocabulary": [
        {
          "word": "친구로",
          "translation": "as a friend"
        },
        {
          "word": "바라다",
          "translation": "look forward to"
        },
        {
          "word": "다시",
          "translation": "again"
        }
      ]
    }
  ]
}
```

### 5.2. Genre-Based Story Invention Example

This test proves that the AI can invent a new story concept based on a genre and generate structured content for it.

#### Prompt
```
You are a creative author writing a simple story for a beginner language learner.


The user has selected the "Mystery" genre.

Your first task is to silently invent a simple, compelling story concept suitable for a beginner learner within this genre.
Your second task is to write the content for the first 4 pages of the story you just invented. The story must be written in **Korean**.

You MUST provide the output in a valid JSON format. The root object should be a `pages` array containing **exactly 4 objects**.

Each object in the array represents a single page and must have the following properties:
- `content`: A single string containing approximately 3 to 5 simple sentences for that specific page, written in Korean.

Do not include any other text, notes, or explanations in your response. Provide only the raw JSON.
```

#### Result
```json
{
  "pages": [
    {
      "content": "마을 한가운데 오래된 우물이 있었어요. 어느 날부터 우물 물이 빨갛게 변했어요. 마을 사람들은 걱정하며 티코라는 마법의 호랑이를 찾았어요."
    },
    {
      "content": "티코는 숲 속에서 나타났어요. 그는 붉은 물에 대해 알고 있었어요. \"우물 아래 땅속에 비밀이 있어요,\" 티코가 말했어요. \"그것을 찾아야 해요.\""
    },
    {
      "content": "아이들과 어른들은 티코의 말대로 우물 근처를 파헤치기 시작했어요. 그때 작은 빨간 돌이 나타났어요. 돌을 움직이니 어둠 속에서 빛나는 작은 문이 나타났어요."
    },
    {
      "content": "티코는 문을 열라고 조언했어요. 문이 열리자 안에서는 신비로운 빛이 발산되었어요. 마을 사람들은 조심스럽게 안으로 들어가 비밀을 찾아냈어요. 그날 이후 우물 물은 다시 맑아졌어요."
    }
  ]
}
```

---

## 6. Remaining Backend Tasks

The backend generation workflow is functionally complete. The final remaining task is to add alt text for images.

-   **AIEngine Refactor (Completed):**
    -   The `AIEngine` has been successfully refactored into two distinct, explicit methods:
        1.  `generate(AIRequest<T> request)`: This method uses the configured Spring AI `ChatClient` to handle all text-based generation prompts (e.g., `STORY_METADATA`, `STORY_PAGES`).
        2.  `generateImages(AIImageRequest<T> request)`: This method uses the configured Spring AI `ImageModel` to handle image generation.
-   **PromptType Enum (Updated):**
    -   The `PromptType` enum is now exclusively for text-based prompts. Image generation is handled via a separate `AIImageRequest` and does not require a `PromptType`.
