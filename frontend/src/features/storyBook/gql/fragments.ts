import { gql } from "graphql-request";

// Copied from lessonBook fragments to keep features independent.
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

export const storyParagraphFragment = gql`
    fragment StoryParagraphFragment on StoryParagraph {
        id
        paragraphNumber
        content
    }
`;

export const storyPageFragment = gql`
    ${wordFragment}
    ${storyParagraphFragment}
    fragment StoryPageFragment on StoryPage {
        __typename
        ... on StoryContentPage {
            id
            pageNumber
            paragraphs {
                ...StoryParagraphFragment
            }
        }
        ... on StoryVocabularyPage {
            id
            pageNumber
            vocabulary {
                ...WordFragment
            }
        }
    }
`;

export const storyChapterFragment = gql`
    ${storyPageFragment}
    fragment StoryChapterFragment on StoryChapter {
        id
        chapterNumber
        title
        nativeTitle
        storyPages {
            ...StoryPageFragment
        }
    }
`;

export const storyBookFragment = gql`
    ${storyChapterFragment}
    fragment StoryBookFragment on StoryBook {
        id
        title
        difficulty
        language
        genre
        storyChapters {
            ...StoryChapterFragment
        }
    }
`;
