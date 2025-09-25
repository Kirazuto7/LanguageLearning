import { gql } from "graphql-request";

export const storyVocabularyItemFragment = gql`
    fragment StoryVocabularyItemFragment on StoryVocabularyItem {
        id
        word
        translation
        pageNumber
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
    ${storyVocabularyItemFragment}
    ${storyParagraphFragment}
    fragment StoryPageFragment on StoryPage {
        __typename
        ... on StoryContentPage {
            id
            pageNumber
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
            pageNumber
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
        chapterNumber
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
        shortStories {
            ...ShortStoryFragment
        }
    }
`;

export const shortStoryShellFragment = gql`
    fragment ShortStoryShellFragment on ShortStory {
        id
        chapterNumber
        title
        nativeTitle
        genre
    }
`;
