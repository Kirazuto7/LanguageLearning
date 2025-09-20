# Project TODO List

A roadmap for building out the LanguageLearning application into a comprehensive, feature-rich platform.

---

### Tier 1: Core Content & Interaction (Highest Priority)

- [ ] **Complete the `storybook` Feature**
    - [ ] Implement backend `StoryBookGraphQlController`.
    - [ ] Implement backend `StoryBookService`.
    - [ ] Implement frontend UI components for displaying stories (handling the `StoryPage` union).
    - [ ] Implement the multi-step AI text generation logic for stories.
    - [ ] Integrate an image generation model (e.g., Stable Diffusion) into the Docker setup.
    - [ ] Update the AI generation workflow to create image prompts from story text.
    - [ ] Add an `imageUrl` field to the `StoryPage` entity, DTO, and GraphQL type.
    - [ ] Update the frontend to display the generated image on content pages.

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
