import {MascotName} from "./types";

// --- Dashboard & Library Interfaces ---

export enum LibraryItemType {
    LESSON_BOOK = 'LESSON_BOOK',
    STORY_BOOK = 'STORY_BOOK'
}

export interface LibraryItemDTO {
    id: number;
    title: string;
    language: string;
    difficulty: string;
    createdAt: string; // ISO 8601 date string (YYYY-MM-DD)
}

export interface LessonBookLibraryItemDTO extends LibraryItemDTO {
    type: LibraryItemType.LESSON_BOOK;
    chapterCount: number;
    pageCount: number;
}

export interface StoryBookLibraryItemDTO extends LibraryItemDTO {
    type: LibraryItemType.STORY_BOOK;
    storyCount: number;
    pageCount: number;
}

export interface UserDTO {
    id: number;
    username: string;
    settings: SettingsDTO;
    lessonBookList: LessonBookDTO[];
}

export interface UserDataDTO {
    lessonBooks: LessonBookLibraryItemDTO[];
    storyBooks: StoryBookLibraryItemDTO[];
}

export interface SettingsDTO {
    id: number;
    language: string;
    difficulty: string;
    theme: string;
    mascot: MascotName;
    autoSpeakEnabled: boolean;
}

// --- Book Interfaces ---

export interface BookDTO {
    id: string;
    title: string;
    difficulty: string;
    language: string;
    createdAt: string; // ISO 8601 date string
}

export interface LessonBookDTO extends BookDTO {
    lessonChapters: LessonChapterDTO[];
}

export interface StoryBookDTO extends BookDTO {
    shortStories: ShortStoryDTO[];
}

// --- Chapter & Story Interfaces ---

export interface ChapterDTO {
    id: string;
    title: string;
    nativeTitle: string;
}

export interface LessonChapterDTO extends ChapterDTO {
    lessonPages: LessonPageDTO[];
}

export interface ShortStoryDTO extends ChapterDTO {
    genre?: string;
    storyPages: StoryPageDTO[];
}

// --- Page Interfaces ---

export enum StoryPageType {
    CONTENT = 'CONTENT',
    VOCABULARY = 'VOCABULARY'
}

export interface PageDTO {
    id: string;
}

export interface LessonPageDTO extends PageDTO {
    __typename: 'LessonPage';
    lesson: AnyLessonDTO;
}

export interface StoryContentPageDTO extends PageDTO {
    __typename: 'StoryContentPage';
    type: StoryPageType.CONTENT;
    englishSummary?: string;
    imageUrl?: string;
    paragraphs: StoryParagraphDTO[];
    vocabulary: StoryVocabularyItemDTO[];
}

export interface StoryVocabularyPageDTO extends PageDTO {
    __typename: 'StoryVocabularyPage';
    type: StoryPageType.VOCABULARY;
    englishSummary?: string;
    vocabulary: StoryVocabularyItemDTO[];
}

export type StoryPageDTO = StoryContentPageDTO | StoryVocabularyPageDTO;

// --- Story-Specific Interfaces ---

export interface StoryParagraphDTO {
    id: string;
    paragraphNumber: number;
    content: string;
    wordsToHighlight: string[];
}

export interface StoryVocabularyItemDTO {
    id: string;
    word: string;
    stem: string;
    translation: string;
}

// --- Lesson-Specific Interfaces ---

export interface LessonDTO {
    id: string;
    title: string;
    type: 'VOCABULARY' | 'GRAMMAR' | 'CONJUGATION' | 'PRACTICE' | 'READING_COMPREHENSION';
}

export interface VocabularyLessonDTO extends LessonDTO {
    __typename: 'VocabularyLesson';
    type: 'VOCABULARY';
    vocabularies: WordDTO[];
}

export interface GrammarLessonDTO extends LessonDTO {
    __typename: 'GrammarLesson';
    type: 'GRAMMAR';
    grammarConcept: string;
    explanation: string;
    exampleLessonSentences: LessonSentenceDTO[];
}

export interface ConjugationLessonDTO extends LessonDTO {
    __typename: 'ConjugationLesson';
    type: 'CONJUGATION';
    conjugationRuleName: string;
    explanation: string;
    conjugatedWords: ConjugationExampleDTO[];
}

export interface PracticeLessonDTO extends LessonDTO {
    __typename: 'PracticeLesson';
    type: 'PRACTICE';
    instructions: string;
    lessonQuestions: LessonQuestionDTO[];
}

export interface ReadingComprehensionLessonDTO extends LessonDTO {
    __typename: 'ReadingComprehensionLesson';
    type: 'READING_COMPREHENSION';
    story: string;
    lessonQuestions: LessonQuestionDTO[];
}

export type AnyLessonDTO = VocabularyLessonDTO | GrammarLessonDTO | ConjugationLessonDTO | PracticeLessonDTO | ReadingComprehensionLessonDTO;

// --- Shared Component Interfaces ---

export interface JapaneseWordDetailsDTO {
    __typename: 'JapaneseWordDetails';
    kanji?: string;
    hiragana: string;
    katakana?: string;
    romaji?: string;
}

export interface KoreanWordDetailsDTO {
    __typename: 'KoreanWordDetails';
    hangul: string;
    hanja?: string;
    romaja?: string;
}

export interface ChineseWordDetailsDTO {
    __typename: 'ChineseWordDetails';
    simplified: string;
    traditional?: string;
    pinyin?: string;
    toneNumber?: string;
}

export interface ThaiWordDetailsDTO {
    __typename: 'ThaiWordDetails';
    thaiScript: string;
    romanization?: string;
    tonePattern?: string;
}

export interface ItalianWordDetailsDTO {
    __typename: 'ItalianWordDetails';
    lemma: string;
    gender?: string;
    pluralForm?: string;
}

export interface SpanishWordDetailsDTO {
    __typename: 'SpanishWordDetails';
    lemma: string;
    gender?: string;
    pluralForm?: string;
}

export interface FrenchWordDetailsDTO {
    __typename: 'FrenchWordDetails';
    lemma: string;
    gender?: string;
    pluralForm?: string;
}

export interface GermanWordDetailsDTO {
    __typename: 'GermanWordDetails';
    lemma: string;
    gender?: string;
    pluralForm?: string;
    separablePrefix?: string;
}

export type WordDetailsDTO =
    | JapaneseWordDetailsDTO
    | KoreanWordDetailsDTO
    | ChineseWordDetailsDTO
    | ThaiWordDetailsDTO
    | ItalianWordDetailsDTO
    | SpanishWordDetailsDTO
    | FrenchWordDetailsDTO
    | GermanWordDetailsDTO;

export interface WordDTO {
    id: string;
    englishTranslation: string;
    language: string;
    details: WordDetailsDTO;
}

export interface LessonSentenceDTO {
    id: string;
    text: string;
    translation: string;
}

export interface LessonQuestionDTO {
    id: string;
    questionType: 'MULTIPLE_CHOICE' | 'FREE_FORM';
    questionText: string;
    answerChoices?: string[];
    answer: string | null;
}

export interface ConjugationExampleDTO {
    id: string;
    infinitive: string;
    conjugatedForm: string;
    exampleSentence: string;
    sentenceTranslation: string;
}

// --- API Request/Response Interfaces ---

export interface CreateUserRequest {
    username: string;
    email: string;
    password: string;
    language: string;
    difficulty: string;
}

export interface CompleteOidcRegistrationRequest {
    onboardingToken: string;
    username: string;
    language: string;
    difficulty: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LessonBookRequest {
    language: string;
    difficulty: string;
}

export interface StoryBookRequest {
    language: string;
    difficulty: string;
}

export interface ChapterGenerationRequest {
    language: string;
    difficulty: string;
    topic: string;
}

export interface ShortStoryGenerationRequest {
    language: string;
    difficulty: string;
    topic?: string;
    genre?: string;
}

export interface AuthenticationResponse {
    token: string;
    user: UserDTO;
}

export interface PracticeLessonCheckRequest {
    language: string;
    difficulty: string;
    questionId: string;
    userSentence: string;
}

export interface PracticeLessonCheckResponse {
    isCorrect: boolean;
    correctedSentence: string;
    feedback: string;
}

// --- Progress & Websocket Interfaces ---

export type ProgressDataDTO = LessonPageDTO | StoryPageDTO;

export function isLessonPageDTO(data: any): data is LessonPageDTO {
    return data && (data as LessonPageDTO).lesson !== undefined;
}

export function isStoryPageDTO(data: any): data is StoryPageDTO {
    return data &&
        ((data.type === StoryPageType.CONTENT && data.__typename === 'StoryContentPage') ||
        (data.type === StoryPageType.VOCABULARY && data.__typename === 'StoryVocabularyPage'));
}

export interface ProgressUpdateDTO {
    taskId: string;
    progress: number;
    message: string;
    data?: ProgressDataDTO;
    isError: boolean;
    isComplete: boolean;
}

export interface TranslationRequest {
    textToTranslate: string;
    sourceLanguage: string;
}

export interface TranslationResponse {
    translatedText: string;
}
