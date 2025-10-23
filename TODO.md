# Project TODO List

- [ ] **Project Renaming: Wayword**
    - [ ] **Decision:** The project will be renamed from "LanguageLearning" to "Wayword".
    - [ ] **Action:** In the future, refactor all relevant project modules, package names, and documentation to reflect the new name.

A roadmap for building out the LanguageLearning application into a comprehensive, feature-rich platform.

---

- [x] **Architectural Refinement: Decouple Display Order from DB**
    - [x] Modify all jOOQ fetch queries to `ORDER BY` the entity `id` instead of `chapterNumber` or `pageNumber`.
    - [x] Remove the application\'s reliance on stored chapter/page numbers for ordering.
    - [x] **Frontend:** Update UI components to calculate and display chapter/page numbers dynamically based on the item\'s index in the sorted list received from the backend.

- [ ] **Decouple jOOQ Generation from Live Database**
    - [ ] **Goal:** Use Testcontainers to spin up an ephemeral PostgreSQL database for the `generateJooq` task. This removes the development dependency on a running database instance.
    - [ ] **Implementation:**
        - [ ] Integrate the Testcontainers Gradle plugin.
        - [ ] Configure the `generateJooq` task to use a containerized database, initialized with the `dev-schema.sql` script.

---

### Tier 1: Core Content & Interaction (Highest Priority)

- [ ] **Complete the `storybook` Feature**
    - [x] Implement the multi-step AI text generation logic for stories.
    - [x] Integrate an image generation model (e.g., Stable Diffusion) into the Docker setup.
    - [x] Update the AI generation workflow to create image prompts from story text.
    - [x] Add an `imageUrl` field to the `StoryPage` entity, DTO, and GraphQL type.
    - [x] **Fix and Verify Data Persistence**: The core issue of saving and fetching nested collections (`paragraphs`, `vocabulary`) is now solved. The `findStoryBookDetailsById` jOOQ query is working correctly.
    - [x] **Optimize Story Persistence with jOOQ Batching**
        - [x] Refactor the iterative, page-by-page persistence logic in `StoryGenerationActions` and `StoryPageService`.
        - [x] Replace it with a single, efficient batch operation that runs after all AI text generation is complete.
        - [x] Use jOOQ\\\'s `batchInsert()` API for inserting all `StoryPage`, `StoryParagraph`, and `StoryVocabularyItem` entities in one go to improve performance.
    - [x] **Fix Image Generation API Integration**
        - [x] Use a pre-built public image for the `image-api` service to ensure a stable API contract.
        - [x] **Configure and use a LoRA** to enforce a consistent art style for all storybook illustrations.
        - [x] **Fix public URL generation for MinIO**
            - [x] Set `public-read` ACL on object upload to ensure images are publicly accessible.
            - [x] Refactor URL generation to dynamically construct the permanent public URL instead of using string replacement on a pre-signed URL.
    - [ ] Design frontend UI to allow story generation via two methods: user-provided topic OR random generation from a selected genre.
    - [x] Implement frontend UI components for displaying stories (handling the `StoryPage` union).
    - [x] Update the frontend to display the generated image on content pages.

- [x] **Implement Robust Client-Side State Management**
    - [x] **Goal:** Persist user sessions across page reloads and synchronize state between multiple open tabs to prevent stale data and misuse.
    - [x] **Refactor & Centralize Progress Handling (High Priority)**
        - [x] **Goal:** Create a single source of truth for managing WebSocket progress subscriptions to eliminate race conditions and simplify component logic.
        - [x] **`ProgressSubscriptionManager`:**
            - [x] Modify it to be the *sole* component responsible for subscribing to and unsubscribing from all active generation tasks found in the `progress` slice of the Redux store.
            - [x] It should dynamically manage subscriptions as tasks are added or removed from the state.
        - [x] **Generation Hooks (`useChapterGeneration`, `useShortStoryGeneration`):**
            - [x] Remove all local state management (`useState`, `useEffect` for cleanup/timeouts) related to subscriptions.
            - [x] Simplify the hooks to only perform two actions: 1) trigger the generation mutation and 2) read progress data directly from the Redux store via selectors.
    - [x] **Persistence (`redux-persist`):**
        - [x] Integrate `redux-persist` to save the Redux store to `localStorage`.
        - [x] Configure it to persist the `auth` and `progress` slices. This keeps the user logged in and ensures orphaned tasks are available for re-subscription on page load.
    - [x] **Synchronization (`BroadcastChannel`):**
        - [x] Implemented a `broadcastMiddleware` to sync state changes (auth, settings, progress) across multiple tabs.
        - [x] Made WebSocket client resilient to disconnections to ensure subscriptions persist across tab closures.
        - [x] Fixed backend `ProgressService` to prevent premature subscription closure.

- [ ] **Fix WebSocket Error Propagation**
    - [ ] **Problem:** When a backend generation task fails (e.g., due to moderation or AI errors), the WebSocket stream closes with a "complete" signal instead of an "error" signal. The client is not being notified of the failure.
    - [ ] **Investigation:** The `ProgressService.sendError` method might be sending the wrong termination signal (`tryEmitComplete()` instead of `tryEmitError()`), or another part of the reactive stream is catching the error and completing gracefully.

- [x] **Implement Multi-Layered Content Moderation**
    - [x] **Layer 1: Client-Side Validation**
        - [x] Created a reusable `useProfanityCheck` hook to encapsulate validation logic.
        - [x] Used `leo-profanity` to provide instant feedback and block submissions directly in the UI.
        - [x] Implemented a global `AlertContext` to display consistent warning messages.
    - [x] **Layer 2: Backend - Primary Moderation**
        - [x] Use the high-quality OpenAI Moderation API as the primary, authoritative check for all user input.
    -- [x] **Layer 3: Backend - Resilient Fallback**
        - [x] Implemented a fallback mechanism for when the primary OpenAI API fails.
        - [x] Use the `lingua` library to accurately detect the language of the user's input text.
        - [x] Use the `com.modernmt.text:profanity-filter` library with the detected language to perform a robust, local profanity check.
    - [x] **Layer 4: Backend - Resilience Patterns**
        - [x] Integrated `Resilience4j` to wrap the OpenAI API calls.
        - [x] Use a `RateLimiter` to proactively prevent `429` errors.
        - [x] Use a `Retry` mechanism to gracefully handle transient network errors or API failures.
    - [ ] Add frontend validation (e.g., `maxLength`) to all user input fields.
    - [ ] Add further backend validation for length and content of all user input.
    - [ ] Engineer all AI prompts to be more "jailbreak resistant" by clearly separating system instructions from user input and adding explicit negative constraints.

- [ ] **User Authentication & Onboarding**
    - [x] Build frontend Login page.
    - [x] Build frontend Register page.
    - [ ] **Implement "My Library" Dashboard & Content Management**
        - [ ] **Dashboard UI:**
            - [ ] Create a new page for the user\'s library/dashboard.
            - [ ] Design horizontal carousels/stacks to display `LessonBooks` and `StoryBooks`.
            - [ ] Fetch and display the 10 most recently created books for each type.
        - [ ] **Content Management UI:**
            - [ ] Create a separate page (e.g., `/my-content`) for managing all generated books.
            - [ ] Display all `LessonBooks` and `StoryBooks` in a list or grid format.
            - [ ] Implement a "Delete" button for each book with a confirmation modal.
        - [ ] **Backend API:**
            - [ ] Create a new GraphQL query (`myLibrary`) to fetch a combined, sorted list of a user\'s books.
            - [ ] Implement a jOOQ repository using `UNION ALL` to efficiently query both `lesson_books` and `story_books` tables.
            - [ ] Create a GraphQL mutation (`deleteBook`) to handle book deletion.

- [ ] **Add Static Foundational Content**
    - [ ] **Goal:** Provide a structured starting point for absolute beginners.
    - [ ] **Content:** Alphabet lessons, basic greetings, number systems for each supported language.
    - [ ] **Implementation:**
        - [ ] Design a new entity/table for static content (e.g., `FoundationalLesson`).
        - [ ] Use the AI engine once (internally or via a script) to generate high-quality lesson content and save it to the database as seed data.
        - [ ] Build a dedicated UI page (e.g., `/learn/japanese/alphabet`) to display this static content.

- [x] **Advanced Vocabulary Highlighting (Backend-Driven)**
    - [x] Implemented a robust highlighting system where the backend performs linguistic analysis to identify conjugated vocabulary words.
    - [x] **Backend:** Added `NlpService` with Apache Lucene for lemmatization. Updated `StoryParagraph` entity and DTO to store `wordsToHighlight` metadata. Updated `StoryPageService` to populate this metadata during story generation.
    - [x] **Frontend:** Updated `StoryContentPage` to use a simple, efficient regex for highlighting based on the backend-provided data.

---

### Tier 2: Enhancing the Learning Experience & Resilience

- [x] **Structural Validation of AI-Generated Word Details**
    - [x] **Description:** Extended the `FuriganaService` to perform pre-validation sanitization on Japanese vocabulary.
    - [x] **Goal:** Ensure linguistic data is correct before being saved.
    - [x] **Implementation:** The service now intercepts the raw AI response, uses the Kuromoji tokenizer to fix inconsistencies (e.g., romaji in a hiragana field), and reconstructs a valid JSON object before the schema validation runs.

- [ ] **Implement Offline Dictionary for Single Word Translation**
    - [ ] **Description:** Create a `DictionaryService` to provide fast, offline, low-cost translations for single words, replacing the AI\'s translation for `Word` entities.
    - [ ] **Goal:** Improve performance, reduce AI costs, and increase translation reliability for individual vocabulary items.
    - [ ] **Resources:**
        - [ ] **Data Source:** [FreeDict](https://freedict.org/)
        - [ ] **Java Library Research:** Find a Java library to parse the DICT format. (See [Stack Overflow Discussion](https://stackoverflow.com/questions/7455513/is-there-a-java-translation-library-that-works-offline)).
        - [ ] **Tools:** The `freedict-tools` package may contain useful utilities. ([Debian Package](https://packages.debian.org/search?keywords=freedict&searchon=names&suite=stable&section=all), [Trixie Package](https://packages.debian.org/trixie/freedict-tools)).

- [ ] **Implement AI Content Validation with Wiktionary**
    - [ ] **Phase 1: Local Setup & Proof of Concept**
        - [x] Create a standalone `wiktionary-importer` tool to parse the Wiktionary data dump.
        - [x] Use JWKTL to generate a local, file-based database from the dump.
        - [ ] Create a `DictionaryValidator` service in the main `backend` app to query the local database.
        - [ ] Integrate the validator into the AI generation workflow to fact-check word translations.
    - [ ] **Phase 2: Production-Ready Database (Future)**
        - [ ] Design a schema for storing dictionary data in a SQL database (e.g., SQLite or a separate PostgreSQL DB).
        - [ ] Create a `WiktionaryExporter` tool to read the local JWKTL database and generate a `.sql` file with INSERT statements.
        - [ ] Use the generated `.sql` file to populate the target SQL database (e.g., SQLite).
        - [ ] Update the `DictionaryValidator` service to connect to and query the new database.

- [ ] **Implement Semantic Caching to Prevent Repetitive Content**
- [ ] **Implement Fallback Translation Service (for Sentences)**
    - [ ] **Goal:** Improve application resilience by providing a simple, reliable translation for **full sentences** when the primary AI generation fails validation or returns malformed data.
    - [ ] **Technology:** Use the `UlionTse/translators` Python library (Link) wrapped in a small, dedicated microservice (e.g., using Flask/FastAPI) within the Docker environment.
    - [ ] **Strategy:**
        - [ ] When a generated lesson or story fails a validation step (e.g., a sentence is malformed), use the fallback service to get a direct, simple translation for the problematic part.
        - [ ] Define a threshold for fallback usage. If too many parts of a generation fail, discard the result and retry the primary AI generation instead of over-relying on the fallback.
        - [ ] This service will act as a safety net, not a primary content generator.

- [ ] **Audio Narration (Text-to-Speech)**
    - [ ] Integrate a third-party Text-to-Speech service (e.g., Google Cloud TTS).
    - [ ] Add a "Play" button to story paragraphs and reading comprehension passages.

- [ ] **User Progress Tracking**
    - [ ] Add a `isCompleted` field to `lesson_chapters` and `story_chapters` tables.
    - [ ] Implement a "Mark as Complete" button in the UI.
    - [ ] Display progress indicators (e.g., a checkmark) on completed books in the user\\'s library.

- [ ] **Implement Semantic Search & Discovery**
    - [ ] **Goal:** Allow users to search for content based on meaning and intent, not just keywords.
    - [ ] **Backend - Indexing:**
        - [ ] Add a `vector` column to searchable entities (e.g., `lessons`, `short_stories`) to store embeddings. Consider using the `pgvector` extension for PostgreSQL for efficient similarity searches.
        - [ ] Create a `ContentIndexingService` to generate and save embeddings for all searchable content (e.g., titles, topics). This can be a background job or run on content creation/update.
    - [ ] **Backend - Querying:**
        - [ ] Create a `SemanticSearchService` that takes a user's text query.
        - [ ] Use the `EmbeddingService` to generate a vector for the user's query.
        - [ ] Perform a cosine similarity search against the indexed vectors in the database to find the most relevant content.
        - [ ] Create a new GraphQL query (e.g., `searchContent`) to expose this functionality.
    - [ ] **Frontend:**
        - [ ] Implement a global search bar in the application.
        - [ ] Create a search results page to display the ranked, semantically relevant content returned from the backend.

---

### Tier 2.5: Monetization & Sustainability

- [ ] **Implement Freemium Subscription Model**
    - [ ] Add a `subscriptionStatus` field to the `User` entity (`FREE`, `PREMIUM`).
    - [ ] Implement a hard limit on the number of saved `LessonBooks` and `StoryBooks` for `FREE` users (e.g., 5 total).
    - [ ] Add logic to the `LessonBookService` and `StoryBookService` to check this limit before content creation.
    - [ ] Create a custom exception (e.g., `ContentLimitExceededException`) to be thrown when the limit is reached.
    - [ ] **Frontend:**
        - [ ] Display the user\\'s current content usage (e.g., "3/5 books used").
        - [ ] When the limit is hit, show a modal prompting the user to upgrade or manage their existing content.
    - [ ] Integrate a payment provider (e.g., Stripe) to handle subscriptions.

- [ ] **Define Premium-Tier Features**
    - [ ] Reserve storybook image generation as a feature for `PREMIUM` subscribers only.
    - [ ] Reserve higher-quality AI models (e.g., `exaone3.5`) for `PREMIUM` subscribers.

- [ ] **Implement Concurrency Limiting for AI Service**
    - [ ] **Goal:** Prevent overwhelming the self-hosted AI service (Ollama) with concurrent requests to ensure stability and predictable performance, especially under load.
    - [ ] **Implementation Idea:**
        - [ ] Create a wrapper service around the `AIEngine`.
        - [ ] Use a `Semaphore` (e.g., from Project Reactor) within the wrapper to limit the number of simultaneous requests that can be sent to the AI service (e.g., limit to 1 or 2).
        - [ ] This will effectively create a request queue, ensuring the GPU resources are not exhausted and each generation task gets the full power of the hardware.

---

### Tier 3: Advanced & "Wow" Features

- [ ] **Gamification**
    - [ ] Design and implement a points or rewards system.
    - [ ] Create UI elements for badges or a daily streak counter.

- [ ] **Spaced Repetition System (SRS)**
    - [ ] Allow users to save vocabulary words to a personal review deck.
    - [ ] Create a new UI/page for the SRS flashcard experience.
    - [ ] Implement the SRS algorithm for scheduling reviews.

- [ ] **Documentation Overhaul**
    - [ ] Create a dedicated section in the `README.md` for each key feature.
    - [ ] Add architectural diagrams for the backend workflows.

---

### Tier 4: Advanced Features (Requires Additional Funding)

- [ ] **Implement Semantic Caching to Prevent Repetitive Content**
    - [ ] **Goal:** Prevent the AI from generating content that is thematically too similar to recently created lessons, improving content diversity.
    - [ ] **Infrastructure:**
        - [ ] Integrate a sentence transformer model (e.g., `all-MiniLM-L6-v2`) into a new, dedicated AI service container to generate embeddings.
        - [ ] Add a new field (e.g., `summary_embedding` of type `vector(384)`) to relevant tables like `grammar_lessons`.
        - [ ] Create a Spring Boot `ApplicationRunner` or similar configuration to run `CREATE EXTENSION IF NOT EXISTS vector;` in PostgreSQL on startup to enable `pgvector`.
    - [ ] **Generation Workflow Modification:**
        - [ ] Before generating a new lesson, create a summary/topic embedding for the proposed content.
        - [ ] Perform a cosine similarity search against the most recent N lessons of the same type, language, and difficulty.
        - [ ] If the similarity for a given chapter type (e.g., a `GrammarLesson`) exceeds a defined threshold, identify the content as a "semantic duplicate".
    - [ ] **AI Feedback Loop:**
        - [ ] If a duplicate is detected, cancel the initial generation.
        - [ ] Re-trigger the AI prompt, adding the summaries of the duplicate lessons as a negative constraint (e.g., "Avoid generating a lesson similar to these topics: ...").
    - [ ] **Persistence:**
        - [ ] When a new, unique lesson is successfully generated and saved, also save its summary embedding to the database.

- [ ] **Documentation Overhaul**
    - [ ] Create a dedicated section in the `README.md` for each key feature.
    - [ ] Add architectural diagrams for the backend workflows.

---

### Admin Console

Create a dedicated admin console for debugging and development monitoring.

#### Architecture

-   **Frontend:** A separate single-page application (e.g., React) running on its own port (e.g., `localhost:3001`).
-   **Backend:** Use the **existing** Spring Boot server.
    -   **Reasoning:** Provides direct access to application services, repositories, and the database context. Operationally simpler and more cost-effective than a separate server.
    -   **CORS:** Configure CORS on the backend to allow requests from the admin frontend\\'s origin.
-   **Code Organization (Backend):**
    -   Place all admin-related controllers, services, and DTOs in a dedicated root package: `com.example.language_learning.admin`.
-   **Production Safety:**
    -   Annotate all admin-related Spring components (`@RestController`, `@Service`, etc.) with `@Profile("!prod")`. This will prevent them from being loaded in the production environment, ensuring no performance overhead or security exposure.

#### Feature Roadmap

1.  **Log Viewer (Phase 1)**
    -   **Goal:** Display real-time application logs from `log4j`.
    -   **Backend:**
        -   Create a WebSocket endpoint (e.g., `/ws/admin/logs`).
        -   Implement a custom `log4j` Appender that captures log events and pushes them to the WebSocket.
        -   Secure the endpoint to be admin-only.
    -   **Frontend:**
        -   A component that establishes a WebSocket connection and displays incoming log messages.
        -   Include features like filtering by log level (INFO, ERROR, etc.) and a search bar.

2.  **Application Metrics Dashboard (Phase 2)**
    -   **Goal:** Visualize real-time application health.
    -   **Backend:**
        -   Integrate **Spring Boot Actuator**.
        -   Expose relevant metrics endpoints (e.g., `/actuator/metrics/jvm.memory.used`, `/actuator/metrics/http.server.requests`).
    -   **Frontend:**
        -   Use a charting library (e.g., Chart.js, Recharts) to create dashboards for JVM Memory, API Error Rates, etc.

3.  **Integration Sandbox / Service Runner (Phase 3)**
    -   **Goal:** Create a UI to manually trigger backend service methods for testing.
    -   **Backend:** Create a generic endpoint (e.g., `/api/admin/execute-service`) that uses Java Reflection to invoke specified service methods with parameters from the request.
    -   **Frontend:** A UI with forms to select a service/method and provide its parameters.