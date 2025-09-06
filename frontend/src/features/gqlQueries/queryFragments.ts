import { gql } from "graphql-request";

export const lessonFragment = gql`
    fragment LessonFragment on Lesson {
        id
        type
        title
        ... on VocabularyLesson {
            vocabularies {
                id
                nativeWord
                englishTranslation
                phoneticSpelling
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