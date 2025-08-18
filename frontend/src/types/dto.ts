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
}

export interface LessonBookDTO {
    id: number;
    bookTitle: string;
    difficulty: string;
    language: string;
    chapters: ChapterDTO[];
}

export interface ChapterDTO {
    id: number;
    title: string;
    nativeTitle: string;
    chapterNumber: number;
    pages: PageDTO[];
}

export interface PageDTO {
    id: number;
    pageNumber: number;
    lesson: AnyLessonDTO;
}

export interface LessonDTO {
    id: number;
    title: string;
    type: 'VOCABULARY' | 'GRAMMAR' | 'PRACTICE' | 'READING_COMPREHENSION';
}

export interface WordDTO {
    id: number;
    type: 'korean' | 'japanese';
    translation: string;
}

export interface KoreanWordDTO extends WordDTO {
    type: 'korean';
    hangeul: string;
    hanja: string;
}

export interface JapaneseWordDTO extends WordDTO {
    type: 'japanese';
    hiragana: string;
    katakana: string;
    kanji: string;
    romaji: string;
}

export type AnyWordDTO = KoreanWordDTO | JapaneseWordDTO;

export interface SentenceDTO {
    id: number;
    text: string;
    translation: string;
}

export interface VocabularyLessonDTO extends LessonDTO {
    type: 'VOCABULARY';
    vocabularies: AnyWordDTO[];
}

export interface GrammarLessonDTO extends LessonDTO {
    type: 'GRAMMAR';
    grammarConcept: string;
    nativeGrammarConcept: string;
    explanation: string;
    exampleSentences: SentenceDTO[];
}

export interface QuestionDTO {
    id: number;
    questionType: 'MULTIPLE_CHOICE' | 'FILL_IN_THE_BLANK' | 'FREE_FORM';
    questionText: string;
    options?: string[];
    answer: string;
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

export type AnyLessonDTO = VocabularyLessonDTO | GrammarLessonDTO | PracticeLessonDTO | ReadingComprehensionLessonDTO;

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
    userId: number;
    language: string;
    difficulty: string;
}

export interface ChapterGenerationRequest {
    language: string;
    difficulty: string;
    topic: string;
    userId: number;
}

export interface UpdateSettingsRequest {
    settings: Partial<Omit<SettingsDTO, 'id'>>;
}

export interface AuthenticationResponse {
    token: string,
    user: UserDTO
}
