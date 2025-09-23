import { gql } from "graphql-request";
import { storyPageFragment } from "./fragments";

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
