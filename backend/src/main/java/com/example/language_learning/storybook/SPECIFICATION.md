# Storybook Feature Specification

This document outlines the design and business logic for the AI-powered storybook generation feature.

---

## 1. Core Architectural Model

-   **`Storybook`:** A `Storybook` acts as a collection or anthology of short stories. Each user has a unique `Storybook` for each `(language, difficulty)` combination (e.g., "Beginner Korean Storybook"). It does not have a genre itself.

-   **`ShortStory`:** A `ShortStory` is the core generated entity. Each `ShortStory` has its own `title`, `genre`/`topic`, and a list of `StoryPages`. It is contained within a parent `Storybook`.

-   **`StoryPage`:** A page within a `ShortStory`. It can be a content page with paragraphs or a vocabulary page. It contains an `imageUrl` field to store the path to its illustration and an `englishSummary` to be used as a prompt for image generation.

---

## 2. Paragraph & Page Definitions

### 2.1. Paragraph
A single **Paragraph** is defined as a block of text containing **3 to 5 sentences**.

### 2.2. Page Content by Difficulty

-   **Beginner Pages:** 1 Paragraph per page. (Prompt for ~3-5 sentences).
-   **Intermediate Pages:** 2 Paragraphs per page. (Prompt for ~6-8 sentences).
-   **Advanced Pages:** 3 Paragraphs per page. (Prompt for ~9-12 sentences).

---

## 3. Generation Workflow

The end-to-end generation of a new `ShortStory` follows a multi-step, multi-prompt process.

1.  **Initiation:** The user, viewing a specific `Storybook`, provides a **Genre** and an optional **Topic**.
2.  **Title Generation (1 Prompt):** The backend sends a prompt to the LLM to generate a `title` and `nativeTitle`.
3.  **Page & Vocabulary Generation (Multiple Prompts):** The backend generates the `content`, `englishSummary`, and `vocabulary` for a batch of pages at a time.
4.  **Image Generation (Cost-Effective Strategy):** To balance visual appeal with API costs, images will be generated for **every other page, starting from page 2**.
    a.  The generation process will happen in a batch after all story pages have been created.
    b.  For each even-numbered page (2, 4, 6, etc.), a prompt will be constructed by combining the `englishSummary` of the **previous page** and the **current page**. This provides richer narrative context for the image AI.
    c.  This combined summary is sent to the Image Generation model.
    d.  The resulting image's URL is saved to the `imageUrl` field of the **current (even-numbered) page**. Odd-numbered pages will have a `null` `imageUrl`.
5.  **Assembly:** The backend service assembles the `ShortStory`. It aggregates vocabulary (removing duplicates), saves the `imageUrl` to each `StoryPage`, and associates everything with the parent `Storybook` before saving to the database.

---

## 4. Image Storage Strategy

To handle image storage in a scalable and professional manner, the application will not store image files directly in the database or project directory.

### 4.1. Development Environment

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
- `pageNumber`: An integer representing the page number (1, 2, 3, 4).
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
      "pageNumber": 1,
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
      "pageNumber": 2,
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
      "pageNumber": 3,
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
      "pageNumber": 4,
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
- `pageNumber`: An integer representing the page number (1, 2, 3, 4).
- `content`: A single string containing approximately 3 to 5 simple sentences for that specific page, written in Korean.

Do not include any other text, notes, or explanations in your response. Provide only the raw JSON.
```

#### Result
```json
{
  "pages": [
    {
      "pageNumber": 1,
      "content": "마을 한가운데 오래된 우물이 있었어요. 어느 날부터 우물 물이 빨갛게 변했어요. 마을 사람들은 걱정하며 티코라는 마법의 호랑이를 찾았어요."
    },
    {
      "pageNumber": 2,
      "content": "티코는 숲 속에서 나타났어요. 그는 붉은 물에 대해 알고 있었어요. \"우물 아래 땅속에 비밀이 있어요,\" 티코가 말했어요. \"그것을 찾아야 해요.\""
    },
    {
      "pageNumber": 3,
      "content": "아이들과 어른들은 티코의 말대로 우물 근처를 파헤치기 시작했어요. 그때 작은 빨간 돌이 나타났어요. 돌을 움직이니 어둠 속에서 빛나는 작은 문이 나타났어요."
    },
    {
      "pageNumber": 4,
      "content": "티코는 문을 열라고 조언했어요. 문이 열리자 안에서는 신비로운 빛이 발산되었어요. 마을 사람들은 조심스럽게 안으로 들어가 비밀을 찾아냈어요. 그날 이후 우물 물은 다시 맑아졌어요."
    }
  ]
}
```
