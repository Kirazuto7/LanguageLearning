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

export interface VocabularyWordDTO {
    id: number;
    word: AnyWordDTO;
    wordIndex: number;
}

export interface SentenceDTO {
    id: number;
    words: AnyWordDTO[];
    text: string;
    translation: string;
}

export interface VocabularyLessonDTO extends LessonDTO {
    type: 'VOCABULARY';
    vocabularies: VocabularyWordDTO[];
}

export interface GrammarLessonDTO extends LessonDTO {
    type: 'GRAMMAR';
    grammarConcept: string;
    explanation: string;
    exampleSentences: SentenceDTO[];
}

export interface QuestionDTO {
    id: number;
    questionType: 'multiple-choice' | 'fill-in-the-blank' | 'free-form';
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




