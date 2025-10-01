import { gql } from "graphql-request";


export const wordFragment = gql`
    fragment WordFragment on Word {
        id
        englishTranslation
        language
        details {
            __typename
            ... on JapaneseWordDetails {
                kanji
                hiragana
                katakana
                romaji
            }
            ... on KoreanWordDetails {
                hangul
                hanja
                romaja
            }
            ... on ChineseWordDetails {
                simplified
                traditional
                pinyin
                toneNumber
            }
            ... on ThaiWordDetails {
                thaiScript
                romanization
                tonePattern
            }
            ... on ItalianWordDetails {
                lemma
                gender
                pluralForm
            }
            ... on SpanishWordDetails {
                lemma
                gender
                pluralForm
            }
            ... on FrenchWordDetails {
                lemma
                gender
                pluralForm
            }
            ... on GermanWordDetails {
                lemma
                gender
                pluralForm
                separablePrefix
            }
        }
    }
`;

export const lessonFragment = gql`
    ${wordFragment}
    fragment LessonFragment on Lesson {
        __typename
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
            explanation
            exampleLessonSentences {
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
            lessonQuestions {
                id
                questionType
                questionText
                answerChoices
                answer
            }
        }
        ... on ReadingComprehensionLesson {
            story
            lessonQuestions {
                id
                questionText
                questionType
                answerChoices
                answer
            }
        }
    }
`;


export const lessonPageFragment = gql`
    ${lessonFragment}
    fragment LessonPageFragment on LessonPage {
        __typename
        id
        lesson {
            __typename
            ...LessonFragment
        }
    }
`;

export const lessonChapterFragment = gql`
    ${lessonPageFragment}
    fragment LessonChapterFragment on LessonChapter {
        id
        title
        nativeTitle
        lessonPages {
            ...LessonPageFragment
        }
    }
`;

export const lessonBookFragment = gql`
    ${lessonChapterFragment}
    fragment LessonBookFragment on LessonBook {
        id
        title
        difficulty
        language
        createdAt
        lessonChapters {
            ...LessonChapterFragment
        }
    }
`;
