# Generation Workflow Specification

This document provides a comprehensive blueprint for the content generation architecture. It details the core patterns and provides a side-by-side comparison of the existing `lessonbook` workflow and the proposed `storybook` workflow.

---

### **Table of Contents**
1.  [Core Architectural Pattern](#1-core-architectural-pattern-orchestrator--domain-services)
2.  [`lessonbook` Chapter Generation Workflow](#2-lessonbook-chapter-generation-workflow)
    *   [Visual Diagram](#visual-diagram-lessonbook)
    *   [Phase 1: Synchronous Setup](#phase-1-synchronous-setup-lessonbook)
    *   [Phase 2: Asynchronous Generation](#phase-2-asynchronous-generation-lessonbook)
3.  [`storybook` Short Story Generation Workflow](#3-storybook-short-story-generation-workflow)
    *   [Visual Diagram](#visual-diagram-storybook)
    *   [Phase 1: Synchronous Setup (Proposed)](#phase-1-synchronous-setup-proposed-storybook)
    *   [Phase 2: Asynchronous Generation (Proposed)](#phase-2-asynchronous-generation-proposed-storybook)
4.  [AI Generation Sub-Workflow (Reactive State Machine)](#4-ai-generation-sub-workflow-reactive-state-machine)

---

## 1. Core Architectural Pattern: Orchestrator & Domain Services

The entire generation process is built on a clean separation of concerns, connected by two key workflow runners:

-   **Orchestration Service** (e.g., `LessonChapterGenerationService`): This service manages the **process**. It knows the sequence of steps and handles the transition from synchronous to asynchronous work. It does not contain business logic for creating or saving entities.

-   **Domain Services** (e.g., `LessonBookService`, `LessonChapterService`): These services manage the **data**. They are simple data access layers responsible for creating, reading, and updating a specific entity. They know nothing about the larger generation workflow.

-   **`SyncWorkflow`**: This bean executes a chain of fast, synchronous actions (defined in a class like `ChapterPrepActions`) to set up the initial state. This is used for the immediate, blocking part of the workflow that must complete before returning a response to the user.

-   **`StateMachine`**: This bean executes a graph of slow, asynchronous actions (defined in a class like `ChapterGenerationActions`). This is used for the long-running background job that involves multiple AI calls and can take a significant amount of time.

This pattern makes the system modular, testable, and provides a responsive user experience by returning a `taskId` immediately while the heavy lifting happens in the background.

---

## 2. `lessonbook` Chapter Generation Workflow

The generation of a new `LessonChapter` is the canonical example of this two-phase process.

### Visual Diagram (lessonbook)

```text
================================  SYNCHRONOUS PHASE  =================================
                                     (Immediate Response)

[ Client ] -> [ Controller ] -> [ LessonChapterGenerationService ] -> [ SyncWorkflow ] -> [ ChapterPrepActions ]
    /|\                                (Orchestrator)                                     (findBook, createShell)
     |
     +-----------------------------------< Response with taskId & shell DTO <------------------------------------+


================================ ASYNCHRONOUS PHASE  =================================
                                     (Background Job)

[ LessonChapterGenerationService ] -> [ JobQueueService ] -> [ StateMachine ] -> [ ChapterGenerationActions ]
                                                                                       (handleMetadata, handleVocab, etc.)
                                                                                                  |
                                                                                                  V
                                                                                        [ ProgressService ] -> (WebSocket) -> [ Client ]
```

### Phase 1: Synchronous Setup (lessonbook)

This phase is orchestrated by `LessonChapterGenerationService` and executed by `SyncWorkflow` calling methods in `ChapterPrepActions`. Its goal is to instantly create a placeholder "shell" chapter.

**Key Action Snippet:**

```java
// Inside ChapterPrepActions.java
public void createInitialChapter(ChapterPrepInput input, ChapterPrepOutput output) {
    int nextChapterNumber = output.getBook().getLessonChapters().stream()
            .mapToInt(LessonChapter::getChapterNumber)
            .max()
            .orElse(0) + 1;
    // The Orchestrator provides the placeholder titles
    LessonChapter newLessonChapter = lessonChapterService.createChapter(output.getBook(), nextChapterNumber, "Generating...", "Generating...");
    output.setLessonChapter(newLessonChapter);
}
```

### Phase 2: Asynchronous Generation (lessonbook)

This phase begins after the synchronous transaction commits. The `StateMachine` executes the methods defined in `ChapterGenerationActions.java`.

**Key Action Snippets:**

1.  **Metadata Generation**: The first AI call to get the real chapter title.

    ```java
    // Inside ChapterGenerationActions.java
    public ChapterGenerationState handleMetadataGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        AIRequest<ChapterMetadataDTO> aiRequest = AIRequest.builder()
                .responseClass(ChapterMetadataDTO.class)
                .promptType(PromptType.METADATA)
                // ... params
                .build();

        ChapterMetadataDTO metadata = aiEngine.generate(aiRequest).block();

        LessonChapter lessonChapter = context.getLessonChapter();
        lessonChapter.setTitle(metadata.title());
        lessonChapter.setNativeTitle(metadata.nativeTitle());
        lessonChapterService.saveChapter(lessonChapter);
        return ChapterGenerationState.VOCABULARY_LESSON(metadata);
    }
    ```

2.  **Vocabulary Lesson Generation**: A subsequent AI call that creates the first `LessonPage`.

    ```java
    // Inside ChapterGenerationActions.java
    public ChapterGenerationState handleVocabularyGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        ChapterMetadataDTO metadataDto = ((ChapterGenerationState.VOCABULARY_LESSON) fromState).metadataDto();

        AIRequest<VocabularyLessonDTO> aiRequest = AIRequest.builder()
                .responseClass(VocabularyLessonDTO.class)
                .promptType(PromptType.VOCABULARY)
                // ... params
                .build();

        VocabularyLessonDTO lessonDto = aiEngine.generate(aiRequest).block();

        // The action creates the page via the domain service
        LessonPage lessonPage = lessonPageService.createAndPersistPage(context.getLessonChapter(), dtoMapper.toEntity(lessonDto), context.getPageCounter().getAndIncrement());

        progressService.sendPageUpdate(context.getTaskId(), 40, "Vocabulary created.", dtoMapper.toDto(lessonPage));
        
        return ChapterGenerationState.GRAMMAR_LESSON(lessonDto);
    }
    ```

---

## 3. `storybook` Short Story Generation Workflow

This workflow adapts the `lessonbook` pattern for the simpler requirements of generating a `ShortStory`.

### Visual Diagram (storybook)

```text
================================  SYNCHRONOUS PHASE  =================================
                                     (Immediate Response)

[ Client ] -> [ Controller ] -> [ StoryGenerationService ] -> [ StoryBookService ] (findOrCreateBook)
    /|\                               (Orchestrator)      |
     |                                                      V
     |                                                [ ShortStoryService ] (createShortStory shell)
     |
     +----------------------< Response with taskId & shell DTO <--------------------------+


================================ ASYNCHRONOUS PHASE  =================================
                                     (Background Job)

[ StoryGenerationService ] -> [ JobQueueService ] -> [ StateMachine ] -> [ StoryPrepActions ]
                                                                             (handleTitle, handlePages)
                                                                                       |
                                                                                       V
                                                                            [ ProgressService ] -> (WebSocket) -> [ Client ]
```

### Phase 1: Synchronous Setup (Proposed) (storybook)

Orchestrated by `StoryGenerationService`.

**Key Action Snippet:**

```java
// Inside StoryGenerationService.java
public StoryGenerationResponse initiateShortStoryGeneration(ShortStoryGenerationRequestInput request, User user) {
    StoryBook storyBook = storyBookService.findOrCreateBook(request.language(), request.difficulty(), user);

    int nextStoryNumber = storyBook.getShortStories().size() + 1;
    String placeholderTitle = "Generating Story...";
    String nativePlaceholder = "..."; // Language-specific placeholder logic here

    ShortStory newStory = shortStoryService.createShortStory(storyBook, nextStoryNumber, request.genre(), placeholderTitle, nativePlaceholder);

    String taskId = UUID.randomUUID().toString();

    // ... schedule async job ...

    return new StoryGenerationResponse(taskId, dtoMapper.toDto(newStory));
}
```

### Phase 2: Asynchronous Generation (Proposed) (storybook)

This phase will be orchestrated by `StoryGenerationService.generateShortStoryAsync` and executed by a new `StateMachine`.

**Proposed States & Actions:**

1.  **`GENERATE_TITLE`**: The first state. Its action will make the first AI call to generate the real `title` and `nativeTitle`. It will then call `shortStoryService.saveShortStory()` to update the entity. A progress update is sent.

2.  **`GENERATE_PAGES`**: The second state. Its action will loop, making AI calls to generate the `content`, `englishSummary`, and `vocabulary` for each `StoryPage`. It will create and save the `StoryPage` and `StoryVocabularyItem` entities and send progress updates for each page.

3.  **`COMPLETE_GENERATION`**: The final state, indicating the process is finished.

---

## 4. AI Generation Sub-Workflow (Reactive State Machine)

This is the low-level, self-correcting workflow that powers every individual call to the AI. It is orchestrated by the `AIEngine` and its actions are defined in `AIGenerationActions.java`. Its purpose is to ensure every AI response is valid, schema-compliant, and sanitized before being returned to the business-level state machine.

### Visual Diagram (AI Sub-Workflow)

```text
[ START ] -> [ GENERATION ] -- Raw AI Response --> [ VALIDATION ] --+
                                     ^                             |
                                     | (Attempts < Max)            |
                                     |                             | (Passes Schema)
                                     +-------- [ RETRYING ] <------+
                                     |               ^             |
(Attempts >= Max)                      |               |             V
                                     |               | (Fix Failed)  [ COMPLETED ] -> [ END ]
                                     V               |
                                  [ FAILED ]         +-- [ SANITIZING ]
                                     ^                     (Fails Schema)
                                     |                           |
                                     +---------------------------+
                                          (JSON Parsing Error)
```

### Reactive States & Actions

This workflow uses a `ReactiveStateMachine` which operates on `Mono<AIGenerationState>` objects, making it fully non-blocking.

1.  **`GENERATION`**: Makes the actual streaming call to the AI chat client.

    ```java
    // Inside AIGenerationActions.java
    public Mono<AIGenerationState> handleGeneration(AIGenerationState fromState, AIGenerationContext context) {
        // ... build user message ...
        return context.chatClient().prompt()
                .user(userMessage)
                .stream().content().collectList()
                .map(list -> String.join("", list).trim())
                .map(AIGenerationState::VALIDATION);
    }
    ```

2.  **`VALIDATION`**: Extracts the JSON from the raw response and validates it against the prompt's JSON schema.

    ```java
    // Inside AIGenerationActions.java
    public Mono<AIGenerationState> handleValidation(AIGenerationState fromState, AIGenerationContext context) {
        String rawResponse = ((AIGenerationState.VALIDATION) fromState).rawResponse();
        String jsonString = sanitizer.extractAndSanitizeJson(rawResponse);
        try {
            JsonNode responseNode = objectMapper.readTree(jsonString);
            JsonSchema schema = jsonSchemaFactory.getSchema(context.aiPrompt().schema());
            Set<ValidationMessage> errors = schema.validate(responseNode);

            if (errors.isEmpty()) {
                Object result = objectMapper.convertValue(responseNode, context.apiDtoType());
                return Mono.just(AIGenerationState.COMPLETED(result));
            } else {
                // ... prepare feedback for sanitizing/retry ...
                return Mono.just(AIGenerationState.SANITIZING(responseNode, errors));
            }
        } catch (JsonProcessingException e) {
            // ... prepare feedback for retry ...
            return Mono.just(AIGenerationState.RETRYING);
        }
    }
    ```

3.  **`SANITIZING`**: If schema validation fails, this state attempts to automatically fix common errors (e.g., trailing commas, incorrect types) before re-validating.

    ```java
    // Inside AIGenerationActions.java
    public Mono<AIGenerationState> handleSanitization(AIGenerationState fromState, AIGenerationContext context) {
        // ... cast state to get node and errors ...
        JsonNode fixedNode = sanitizer.sanitizeJsonValidationErrors(responseNode, errors, schema);
        Set<ValidationMessage> newErrors = schema.validate(fixedNode);

        if (newErrors.isEmpty()) {
            // ... convert to DTO and complete ...
            return Mono.just(AIGenerationState.COMPLETED(result));
        } else {
            // ... prepare feedback for retry ...
            return Mono.just(AIGenerationState.RETRYING);
        }
    }
    ```

4.  **`RETRYING`**: If validation or sanitization fails, this state increments a counter. If the max retries have not been reached, it transitions back to `GENERATION`, but this time the prompt includes specific feedback about what went wrong with the previous attempt.

    ```java
    // Inside AIGenerationActions.java
    public Mono<AIGenerationState> handleRetry(AIGenerationState fromState, AIGenerationContext context) {
        if (context.attemptCounter().incrementAndGet() > context.maxRetries()) {
            return Mono.just(new AIGenerationState.FAILED(...));
        }
        return Mono.just(AIGenerationState.GENERATION);
    }
    ```
