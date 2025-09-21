import {MascotName} from "./types";

export interface UserDTO {
    id: number;
    username: string;
    settings: SettingsDTO;
    lessonBookList: LessonBookDTO[];
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
    chapterNumber: number;
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

export interface PageDTO {
    id: string;
    pageNumber: number;
}

export interface LessonPageDTO extends PageDTO {
    lesson: AnyLessonDTO;
}

export interface StoryContentPageDTO extends PageDTO {
    __typename: 'StoryContentPage';
    englishSummary?: string;
    imageUrl?: string;
    paragraphs: StoryParagraphDTO[];
}

export interface StoryVocabularyPageDTO extends PageDTO {
    __typename: 'StoryVocabularyPage';
    englishSummary?: string;
    imageUrl?: string;
    vocabulary: StoryVocabularyItemDTO[];
}

export type StoryPageDTO = StoryContentPageDTO | StoryVocabularyPageDTO;

// --- Story-Specific Interfaces ---

export interface StoryParagraphDTO {
    id: string;
    paragraphNumber: number;
    content: string;
}

export interface StoryVocabularyItemDTO {
    id: string;
    word: string;
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
    exampleSentences: SentenceDTO[];
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
    questions: QuestionDTO[];
}

export interface ReadingComprehensionLessonDTO extends LessonDTO {
    __typename: 'ReadingComprehensionLesson';
    type: 'READING_COMPREHENSION';
    story: string;
    questions: QuestionDTO[];
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

export interface SentenceDTO {
    id: string;
    text: string;
    translation: string;
}

export interface QuestionDTO {
    id: string;
    questionType: 'MULTIPLE_CHOICE' | 'FREE_FORM';
    questionText: string;
    answerChoices?: string[];
    answer: string;
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
    password: string;
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
    return data && ((data as StoryContentPageDTO).paragraphs !== undefined || (data as StoryVocabularyPageDTO).vocabulary !== undefined);
}

export interface ProgressUpdateDTO {
    taskId: string;
    progress: number;
    message: string;
    data?: ProgressDataDTO;
    error?: string;
    isComplete: boolean;
}

export interface TranslationRequest {
    textToTranslate: string;
    sourceLanguage: string;
}

export interface TranslationResponse {
    translatedText: string;
}
