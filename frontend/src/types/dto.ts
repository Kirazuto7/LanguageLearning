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

export interface LessonBookDTO {
    id: string;
    bookTitle: string;
    difficulty: string;
    language: string;
    chapters: ChapterDTO[];
}

export interface ChapterDTO {
    id: string;
    title: string;
    nativeTitle: string;
    chapterNumber: number;
    pages: PageDTO[];
}

export interface PageDTO {
    id: string;
    pageNumber: number;
    lesson: AnyLessonDTO;
}

export interface LessonDTO {
    id: string;
    title: string;
    type: 'VOCABULARY' | 'GRAMMAR' | 'CONJUGATION' | 'PRACTICE' | 'READING_COMPREHENSION';
}

export interface GenericWordDetailsDTO {
    __typename: 'GenericWordDetails';
    nativeWord: string;
    phoneticSpelling: string;
}

export interface JapaneseWordDetailsDTO {
    __typename: 'JapaneseWordDetails';
    kanji?: string;
    hiragana: string;
    katakana?: string;
    romaji?: string;
}

export type WordDetailsDTO = GenericWordDetailsDTO | JapaneseWordDetailsDTO;

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

export interface VocabularyLessonDTO extends LessonDTO {
    type: 'VOCABULARY';
    vocabularies: WordDTO[];
}

export interface GrammarLessonDTO extends LessonDTO {
    type: 'GRAMMAR';
    grammarConcept: string;
    nativeGrammarConcept: string;
    explanation: string;
    exampleSentences: SentenceDTO[];
}

export interface QuestionDTO {
    id: string;
    questionType: 'MULTIPLE_CHOICE' | 'FREE_FORM';
    questionText: string;
    answerChoices?: string[];
    answer: string;
}

export interface ConjugationLessonDTO extends LessonDTO {
    type: 'CONJUGATION';
    conjugationRuleName: string;
    explanation: string;
    conjugatedWords: ConjugationExampleDTO[];
}

export interface ConjugationExampleDTO {
    id: string;
    infinitive: string;
    conjugatedForm: string;
    exampleSentence: string;
    sentenceTranslation: string;
}

export interface PracticeLessonDTO extends LessonDTO {
    type: 'PRACTICE';
    instructions: string;
    questions: QuestionDTO[];
}

export interface ReadingComprehensionLessonDTO extends LessonDTO {
    type: 'READING_COMPREHENSION';
    story: string;
    questions: QuestionDTO[];
}

export type AnyLessonDTO = VocabularyLessonDTO | GrammarLessonDTO | ConjugationLessonDTO | PracticeLessonDTO | ReadingComprehensionLessonDTO;

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

export interface ChapterGenerationRequest {
    language: string;
    difficulty: string;
    topic: string;
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

export type ProgressDataDTO = PageDTO;

export function isPageDTO(data: any): boolean {
    return data && typeof data.pageNumber === 'number' && data.lesson !== undefined;
}

export interface ProgressUpdateDTO {
    taskId: string;
    progress: number;
    message: string;
    data?: ProgressDataDTO;
    error?: string;
    isComplete: boolean;
}
