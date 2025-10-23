import { gql } from "graphql-request";

export const storyVocabularyItemFragment = gql`
    fragment StoryVocabularyItemFragment on StoryVocabularyItem {
        id
        word
        stem
        translation
    }
`;

export const storyParagraphFragment = gql`
    fragment StoryParagraphFragment on StoryParagraph {
        id
        paragraphNumber
        content
        wordsToHighlight
    }
`;

export const storyPageFragment = gql`
    ${storyVocabularyItemFragment}
    ${storyParagraphFragment}
    fragment StoryPageFragment on StoryPage {
        __typename
        ... on StoryContentPage {
            id
            englishSummary
            imageUrl
            type
            paragraphs {
                ...StoryParagraphFragment
            }
            vocabulary {
                ...StoryVocabularyItemFragment
            }
        }
        ... on StoryVocabularyPage {
            id
            englishSummary
            type
            vocabulary {
                ...StoryVocabularyItemFragment
            }
        }
    }
`;

export const shortStoryFragment = gql`
    ${storyPageFragment}
    fragment ShortStoryFragment on ShortStory {
        id
        title
        nativeTitle
        genre
        storyPages {
            ...StoryPageFragment
        }
    }
`;

export const storyBookFragment = gql`
    ${shortStoryFragment}
    fragment StoryBookFragment on StoryBook {
        id
        title
        difficulty
        language
        createdAt
        shortStories {
            ...ShortStoryFragment
        }
    }
`;

export const shortStoryShellFragment = gql`
    fragment ShortStoryShellFragment on ShortStory {
        id
        title
        nativeTitle
        genre
    }
`;
