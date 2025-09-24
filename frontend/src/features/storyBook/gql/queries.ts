import { gql } from "graphql-request";
import {shortStoryFragment, storyBookFragment, storyPageFragment} from "./fragments";

export const shortStoryGenerationProgressQuery = gql`
    ${storyPageFragment}
    subscription ShortStoryGenerationProgress($taskId: ID!) {
        shortStoryGenerationProgress(taskId: $taskId) {
            taskId
            progress
            message
            isComplete
            data {
                __typename
                ... on StoryPage {
                    ...StoryPageFragment
                }
            }
            error
        }
    }
`;

export const getStoryBooks = gql`
    query GetStoryBooks {
        getStoryBooks {
            id
            title
            difficulty
            language
        }
    }
`;

export const getStoryBook = gql`
    ${storyBookFragment}
    query GetStoryBook($language: String!, $difficulty: String!) {
        getStoryBook(language: $language, difficulty: $difficulty) {
            ...StoryBookFragment
        }
    }
`;
