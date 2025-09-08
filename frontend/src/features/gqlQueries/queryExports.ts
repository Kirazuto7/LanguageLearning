import { gql } from "graphql-request";
import { pageFragment } from "./queryFragments";
export const chapterGenerationProgressQuery = gql`
    ${pageFragment}
    subscription ChapterGenerationProgress($taskId: ID!) {
        chapterGenerationProgress(taskId: $taskId) {
            taskId
            progress
            message
            isComplete
            data {
                __typename
                ...on Page{
                    ...PageFragment
                }
            }
            error
        }
    }
`;