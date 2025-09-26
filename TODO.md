# Project TODO List

A roadmap for building out the LanguageLearning application into a comprehensive, feature-rich platform.

---

### Tier 1: Core Content & Interaction (Highest Priority)

- [ ] **Complete the `storybook` Feature**
    - [x] Implement the multi-step AI text generation logic for stories.
    - [x] Integrate an image generation model (e.g., Stable Diffusion) into the Docker setup.
    - [x] Update the AI generation workflow to create image prompts from story text.
    - [x] Add an `imageUrl` field to the `StoryPage` entity, DTO, and GraphQL type.
    - [ ] **Fix Image Generation API Integration**
        - [ ] Create a custom Dockerfile for the `image-api` service to host a standard Automatic1111 instance, ensuring a stable API contract.
    - [ ] Design frontend UI to allow story generation via two methods: user-provided topic OR random generation from a selected genre.
    - [ ] Implement frontend UI components for displaying stories (handling the `StoryPage` union).
    - [ ] Update the frontend to display the generated image on content pages.

- [ ] **Secure AI Interaction**
    - [ ] Add frontend validation (e.g., `maxLength`) to all user input fields that are sent to the AI.
    - [ ] Add backend validation for length and content of all user input.
    - [ ] Engineer all AI prompts to be "jailbreak resistant" by clearly separating system instructions from user input and adding explicit negative constraints.
    - [ ] Implement a self-validation step in prompts where the AI first checks if the user input is valid before proceeding.

- [ ] **User Authentication & Onboarding**
    - [ ] Build frontend Login page.
    - [ ] Build frontend Register page.
    - [ ] Create a "My Library" or "Dashboard" page to display a user's generated books.

- [ ] **Interactive Vocabulary Practice**
    - [ ] Make vocabulary words in lessons/stories clickable.
    - [ ] On click, show a modal or popover with detailed definitions and examples.
    - [ ] Integrate text-to-speech for word pronunciation on click.

---

### Tier 2: Enhancing the Learning Experience

- [ ] **Audio Narration (Text-to-Speech)**
    - [ ] Integrate a third-party Text-to-Speech service (e.g., Google Cloud TTS).
    - [ ] Add a "Play" button to story paragraphs and reading comprehension passages.

- [ ] **User Progress Tracking**
    - [ ] Add a `isCompleted` field to `LessonChapter` and `StoryChapter` entities.
    - [ ] Implement a "Mark as Complete" button in the UI.
    - [ ] Display progress indicators (e.g., a checkmark) on completed books in the user's library.

- [ ] **Search & Discovery**
    - [ ] Implement a search bar on the frontend.
    - [ ] Add logic to filter the user's generated books by title or topic.

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

### Admin Console

Create a dedicated admin console for debugging and development monitoring.

#### Architecture

-   **Frontend:** A separate single-page application (e.g., React) running on its own port (e.g., `localhost:3001`).
-   **Backend:** Use the **existing** Spring Boot server.
    -   **Reasoning:** Provides direct access to application services, repositories, and the database context. Operationally simpler and more cost-effective than a separate server.
    -   **CORS:** Configure CORS on the backend to allow requests from the admin frontend's origin.
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
