# Storybook Feature Specification

This document outlines the design and business logic for the AI-powered storybook generation feature.

---

## 1. Core Architectural Model

-   **`Storybook`:** A `Storybook` acts as a collection or anthology of short stories. Each user has a unique `Storybook` for each `(language, difficulty)` combination (e.g., "Beginner Korean Storybook"). It does not have a genre itself.

-   **`ShortStory`:** A `ShortStory` is the core generated entity. Each `ShortStory` has its own `title`, `genre`/`topic`, and a list of `StoryPages`. It is contained within a parent `Storybook`.

-   **`StoryPage`:** A page within a `ShortStory`. It can be a content page with paragraphs or a vocabulary page.

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

1.  **Initiation:** The user, viewing a specific `Storybook` (e.g., "Beginner Korean"), provides either a **Topic** or selects a **Genre**.
2.  **Title Generation (1 Prompt):** The backend sends a prompt to the LLM to generate a `title` and `nativeTitle` for the new `ShortStory`.
3.  **Page & Vocabulary Generation (Multiple Prompts):** The backend loops, generating the `content` and `vocabulary` for a batch of pages at a time (e.g., 4 pages per prompt).
4.  **Assembly:** The backend service receives the JSON response. It aggregates the vocabulary from all pages (removing duplicates) and assembles the title, content pages, and a final vocabulary page into a complete `ShortStory` entity. This new `ShortStory` is then associated with its parent `Storybook` and saved.

**(Future) 5. Image Generation:** For each content page, a separate prompt will be sent to an Image Generation model to create an illustration.

---

## 4. Prompt Examples & Test Results

This section contains successful AI responses from the design and testing phase, demonstrating the two primary generation paths.

### 4.1. User-Provided Topic Example (Combined Content & Vocabulary)

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

### 4.2. Genre-Based Story Invention Example

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
