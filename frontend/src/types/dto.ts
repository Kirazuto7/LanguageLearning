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
    type: 'VOCABULARY' | 'GRAMMAR' | 'CONJUGATION' | 'PRACTICE' | 'READING_COMPREHENSION';
}

export interface WordDTO {
    id: number;
    englishTranslation: string;
    language: string;
    nativeWord: string;
    phoneticSpelling: string;
    details?: { [key: string]: any };
}

export interface SentenceDTO {
    id: number;
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
    id: number;
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
    id: number;
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

export interface AuthenticationResponse {
    token: string,
    user: UserDTO
}

export interface PracticeLessonCheckRequest {
    language: string,
    difficulty: string,
    questionId: number,
    userSentence: string
}

export interface PracticeLessonCheckResponse {
    isCorrect: boolean,
    correctedSentence: string,
    feedback: string
}
