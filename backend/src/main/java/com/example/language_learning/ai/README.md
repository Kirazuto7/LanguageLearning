# AI Engine Architecture

## Table of Contents
1. [Overview](#1-overview)
2. [High-Level Architecture Diagram](#2-high-level-architecture-diagram)
3. [Core Components](#3-core-components)
4. [Configuration System](#4-configuration-system)
5. [Environment and Deployment](#5-environment-and-deployment)
6. [How to Add a New Language](#6-how-to-add-a-new-language)

---

## 1. Overview

The AI engine is designed to be a robust, type-safe, and extensible system for interacting with various AI models to generate language-learning content. It abstracts the complexities of prompt engineering, response validation, and data mapping behind a clean and simple API.

The architecture is built on several key patterns:
-   **Registry Pattern:** Decouples the core engine from the logic of how to handle different types of AI responses.
-   **State Machine:** Manages the lifecycle of an AI generation request, including validation, sanitization, and retry logic.
-   **Dynamic Configuration:** Uses a type-safe, enum-driven system to load all necessary resources (prompts, schemas) at startup, avoiding hardcoded paths and brittle configurations.

---
## 2. High-Level Architecture Diagram

This diagram shows the main components and their interactions. It is split into two main phases: **Configuration & Setup** (which happens once at application startup) and **Runtime Interaction** (which happens for each AI request).

```
  +--------------------------------+      +--------------------------------+      +-----------------------------------------------------------------+
  |      Configuration Files       |      |      Startup Components        |      |             Runtime Flow (Chapter Generation Example)           |
  +--------------------------------+      +--------------------------------+      +-----------------------------------------------------------------+

 [application-*.yml] ------------> [AIChatClientConfig] ----------------------> [ChatClient Beans]
 (CPU/GPU Model Tags)                                                          ('qwen3', 'exaone')


 [/prompts/*] -------------------> [AIConfig]
 (Instructions, Schemas)                   |
                                           v
                                   [In-Memory AIAsset Cache]

 [Manual Mappers] ---------------> [AIResponseMapperRegistry]
 (AILessonMapper, etc.)                    |
                                           v
                                   [In-Memory Mappings]


                                                                                 [GraphQL Client]
                                                                                        ^
                                                                                        | 11. Data sent via WebSocket
                                                                                        |
                                                                                 [GraphQL Controller]
                                                                                        ^
                                                                                        | 10. Publishes result
                                                                                        |
                                                                                 [ChapterGenerationService]
                                                                                        | 1. Mutation received
                                                                                        | 2. Creates & Runs
                                                                                        v
                                                                                 +------------------+
                                                                                 |   State Machine  | -- 3. Calls generate() --> +----------+
                                                                                 | (e.g., METADATA) |                            | AIEngine |
                                                                                 +------------------+ <---- 9. Returns DTO ------ +----------+
                                                                                                                                     | ^  |
                                                                                                                                     | |  | 4. Gets Config/Mapper
                                                                                                                                     v |
                                                                                                                             [AIConfig, Registry]
                                                                                                                                     |
                                                                                                                                     | 5. Selects & Calls
                                                                                                                                     v
                                                                                                                               [ChatClient Bean]
                                                                                                                                     ^
                                                                                                                                     | 7. HTTP Response
                                                                                                                                     v 6. HTTP Request
                                                                                                                               (Ollama Server)
```

---

## 3. Core Components

### `AIEngine.java`

This is the primary public-facing service for all AI interactions. Its main responsibility is to orchestrate the entire process from request to final, mapped DTO.

**Key Method:** `public <T_INTERNAL> Mono<T_INTERNAL> generate(AIRequest<T_INTERNAL> request)`

1.  **Receives an `AIRequest`:** This simple object describes the user's intent (e.g., generate vocabulary for Korean).
2.  **Looks up Mapping Strategy:** It queries the `AIResponseMapperRegistry` using the `PromptType` from the request to get the correct "recipe" for handling the response.
3.  **Selects Chat Client:** It uses the `AIConfig` to determine the correct `ChatClient` (e.g., `exaone` or `qwen3`) based on the requested language.
4.  **Executes State Machine:** It runs a reactive state machine to handle the generation, validation, and sanitization of the AI response.
5.  **Maps the Result:** Once a valid AI response is received, it uses the mapper function from the registry to convert the raw AI DTO into the final, internal DTO expected by the caller.

### `AIResponseMapperRegistry.java`

This class implements the **Registry pattern**. Its purpose is to hold all the strategies for mapping raw AI responses to the application's internal DTOs.

-   On startup (`@PostConstruct`), it registers an `AIResponseMapping` for every `PromptType`.
-   Each `AIResponseMapping` contains two key functions:
    1.  A `javaTypeProvider` to tell the JSON parser what kind of object to expect from the AI (e.g., `AIVocabularyLessonResponse<AIKoreanVocabularyItemDTO>`). This is crucial for handling generic types correctly.
    2.  A `mapper` function that takes the raw AI DTO and converts it to the final internal DTO.
-   This decouples the `AIEngine` from the mapping implementation, making the system easy to extend.

### `AIConfig.java`

This is the central configuration hub for the AI system. It replaces a brittle system of dozens of `@Value` annotations with a dynamic, `ResourceLoader`-based approach.

-   **Dynamic Resource Loading:** On startup (`@PostConstruct`), it iterates through all defined `Language` and `PromptType` enums.
-   For each combination, it dynamically constructs the file paths for the required instruction prompts and JSON schemas.
-   **In-Memory Cache:** It reads and parses all these resources once at startup and stores them in an in-memory `Map<String, AIAsset>`. This ensures there is no file I/O at runtime.
-   It provides helper methods like `getPrompt()`, `getModelName()`, and `getVocabularyItemDtoClass()` for the `AIEngine` to retrieve configuration details at runtime.

### `AIChatClientConfig.java`

To ensure full control over model configuration and avoid auto-configuration conflicts, the system uses manual bean creation for all `ChatClient` instances.

-   **Properties Classes:** `Qwen3AIProperties.java` and `ExaoneAIProperties.java` are `@ConfigurationProperties` records that load model-specific settings (like `baseUrl`, `model` tag, and `temperature`) from the `application.yml` files.
-   **Manual Bean Creation:** The `AIChatClientConfig` class uses these properties to:
    1.  Build a distinct `OllamaApi` instance for each model.
    2.  Use the `OllamaApi` to build a corresponding `OllamaChatModel` with the correct default options.
    3.  Finally, create the named `ChatClient` beans (`@Bean("qwen3")`, `@Bean("exaone")`) that are injected into the `AIEngine`.

### `AIGenerationActions.java` & State Machine

The actual AI generation process is managed by a reactive state machine. The `AIGenerationActions` class defines the logic for each state:

-   **`GENERATION`:** Builds the user prompt (including retry feedback if necessary) and sends it to the AI model.
-   **`VALIDATION`:** Receives the raw AI response, extracts the JSON, and validates it against the corresponding JSON schema.
    -   If valid, the process moves to `COMPLETED`.
    -   If the JSON is malformed, it moves to `RETRYING`.
    -   If the schema validation fails, it moves to `SANITIZING`.
-   **`SANITIZING`:** Attempts to automatically fix common schema validation errors.
    -   If successful, it moves to `COMPLETED`.
    -   If it still fails, it moves to `RETRYING`.
-   **`RETRYING`:** Increments a retry counter. If the maximum number of retries has not been exceeded, it transitions back to `GENERATION` with added feedback in the prompt telling the AI what it did wrong.
-   **`COMPLETED` / `FAILED`:** Terminal states that either complete the process successfully with a result or fail with an error.

### Mapper Components (`/ai/mappers/`)

To keep the AI system self-contained, all mappers responsible for converting raw AI DTOs into the application's internal DTOs are located in the `com.example.language_learning.ai.mappers` package.

-   **`AILessonMapper.java`**: Handles the mapping for complex lesson objects (e.g., `VocabularyLesson`, `GrammarLesson`).
-   **`AIWordMapper.java`**: Provides polymorphic mapping for different language-specific vocabulary items into a common `WordDTO`.
-   **`AIResponseMapper.java`**: Maps simple, non-lesson-specific AI responses like translations and proofreading results.

### Configuration Models (`/ai/config/model/`)

These records serve as the in-memory representation of the AI configuration loaded by `AIConfig`.

-   **`AIPrompt.java`**: A simple record holding the `Resource` for an instruction prompt and its corresponding, pre-parsed `JsonNode` schema.
-   **`AIAsset.java`**: Represents the complete set of assets for a given language, containing the model name and a map of all its `AIPrompt`s, keyed by `PromptType`.

---

## 4. Configuration System

The configuration is driven by a set of enums that create a type-safe and maintainable structure.

### Key Enums (`/enums/ai/`)

-   **`Language.java`:** The master enum. It defines every supported language and links it to its required resources:
    -   `modelName`: The name of the `ChatClient` bean to use (e.g., "exaone").
    -   `schemaGroup`: The set of schemas to use for most lessons (e.g., `SchemaGroup.LATIN_EXTENDED`).
    -   `vocabularySchema`: The specific schema for vocabulary lessons.
    -   `instructionGroup`: The specific instruction file for vocabulary lessons.

    ```java
    // Example from Language.java
    ITALIAN("italian", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.ITALIAN, InstructionGroup.ITALIAN),
    KOREAN("korean", "exaone", SchemaGroup.KOREAN, VocabularySchema.KOREAN, InstructionGroup.KOREAN),
    ```

-   **`PromptType.java`:** Defines every possible AI task and provides the path fragments needed to locate its resources.

    ```java
    // Example from PromptType.java
    METADATA("lessons/metadata", "chapter_metadata"),
    VOCABULARY("lessons/vocabulary", "vocabulary_lesson"),
    ```

-   **`SchemaGroup.java`**, **`VocabularySchema.java`**, **`InstructionGroup.java`**: These provide the specific path values used to construct the final resource paths, enabling resource sharing (e.g., `latin_extended` schemas).

### File Structure

The dynamic resource loading relies on a consistent file structure within `src/main/resources/prompts/`:

```
prompts/
├── lessons/
│   ├── metadata/
│   │   ├── instructions/
│   │   │   └── chapter_metadata_prompt.txt
│   │   └── schemas/
│   │       ├── japanese_chapter_metadata_schema.json
│   │       └── latin_extended_chapter_metadata_schema.json
│   └── vocabulary/
│       ├── instructions/
│       │   ├── german_vocabulary_lesson_prompt.txt
│       │   └── japanese_vocabulary_lesson_prompt.txt
│       └── schemas/
│           ├── german_vocabulary_lesson_schema.json
│           └── japanese_vocabulary_lesson_schema.json
└── translation/
    ├── instructions/
    │   └── translation_prompt.txt
    └── schemas/
        └── translation_schema.json
```

---

## 5. Environment and Deployment

The AI engine's configuration is tightly coupled with its deployment environment, particularly how the AI models are made available.

### Docker Entrypoint (`ai_entrypoint.sh`)

The `ai` service container uses an entrypoint script to prepare the Ollama environment at startup.

-   **Profile-based Model Pulling:** On the first startup for a given profile, the script pulls the appropriate AI models based on the `AI_PROFILE` environment variable (`cpu` or `gpu`). This ensures that the container has the correct models available for the intended hardware.

    ```sh
    # Example logic from ai_entrypoint.sh
    if [ "$AI_PROFILE" = "gpu" ]; then
      ollama pull qwen3:8b
      ollama pull exaone3.5:7.8b
    else
      ollama pull qwen3:4b
      ollama pull exaone3.5:2.4b
    fi
    ```

-   **Idempotency:** The script creates a profile-specific marker file (e.g., `/root/.ollama/models_pulled_gpu.marker`) after a successful pull. This prevents the script from re-downloading models on every container restart, speeding up subsequent startups.

### Spring Profile Configuration

The Spring Boot application uses profiles (`cpu` or `gpu`) to align its configuration with the environment provided by the Docker container.

-   **`application-cpu.yml` & `application-gpu.yml`:** These files override the base configuration to specify the correct model tags for the `ollama1` (qwen3) and `ollama2` (exaone) clients. This is critical for ensuring the application requests the exact same models that were pulled by the entrypoint script.

    ```yaml
    # Example from application-gpu.yml
    spring:
      ai:
        ollama1:
          chat:
            model: qwen3:8b
        ollama2:
          chat:
            model: exaone3.5:7.8b
    ```

---

## 6. How to Add a New Language

Adding a new language (e.g., "Portuguese") is now a straightforward process:

1.  **Update Enums:**
    -   Add `PORTUGUESE("portuguese")` to `VocabularySchema.java`.
    -   Add `PORTUGUESE("portuguese")` to `InstructionGroup.java`.
    -   Add the new language to `Language.java`, specifying its model and resource groups. For example:
        ```java
        PORTUGUESE("portuguese", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.PORTUGUESE, InstructionGroup.PORTUGUESE),
        ```

2.  **Add Resource Files:**
    -   Create a new vocabulary instruction file: `resources/prompts/lessons/vocabulary/instructions/portuguese_vocabulary_lesson_prompt.txt`.
    -   Create a new vocabulary schema file: `resources/prompts/lessons/vocabulary/schemas/portuguese_vocabulary_lesson_schema.json`.
    -   *Note: Since Portuguese uses the `LATIN_EXTENDED` schema group, no new schema files are needed for other lesson types.*

3.  **Update Configuration & Mappers:**
    -   In `AIConfig.java`, add the new DTO class to `initializeVocabularyMap()`:
        ```java
        vocabularyItemDtoMap.put("portuguese", AIPortugueseVocabularyItemDTO.class);
        ```
    -   In `AIWordMapper.java`, add a new case to the `toWordDTO` switch to handle the new DTO type.

The system is now configured to support Portuguese without any changes to the core engine logic.
```