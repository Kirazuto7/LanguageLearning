import { gql } from "graphql-request";


export const wordFragment = gql`
    fragment WordFragment on Word {
        id
        englishTranslation
        language
        details {
            __typename
            ... on GenericWordDetails {
                nativeWord
                phoneticSpelling
            }
            ... on JapaneseWordDetails {
                kanji
                hiragana
                katakana
                romaji
            }
        }
    }
`;

export const lessonFragment = gql`
    ${wordFragment}
    fragment LessonFragment on Lesson {
        id
        type
        title
        ... on VocabularyLesson {
            vocabularies {
                ...WordFragment
            }
        }
        ... on GrammarLesson {
            grammarConcept
            nativeGrammarConcept
            explanation
            exampleSentences {
                id
                text
                translation
            }
        }
        ... on ConjugationLesson {
            conjugationRuleName
            explanation
            conjugatedWords {
                id
                infinitive
                conjugatedForm
                exampleSentence
                sentenceTranslation
            }
        }
        ... on PracticeLesson {
            instructions
            questions {
                id
                questionType
                questionText
                answerChoices
                answer
            }
        }
        ... on ReadingComprehensionLesson {
            story
            questions {
                id
                questionText
                questionType
                answerChoices
                answer
            }
        }
    }
`;


export const pageFragment = gql`
    ${lessonFragment}
    fragment PageFragment on Page {
        id
        pageNumber
        lesson {
            ...LessonFragment
        }
    }
`;

export const chapterFragment = gql`
    ${pageFragment}
    fragment ChapterFragment on Chapter {
        id
        chapterNumber
        title
        nativeTitle
        pages {
            ...PageFragment
        }
    }
`;

export const lessonBookFragment = gql`
    ${chapterFragment}
    fragment LessonBookFragment on LessonBook {
        id
        bookTitle
        difficulty
        language
        chapters {
            ...ChapterFragment
        }
    }
`;