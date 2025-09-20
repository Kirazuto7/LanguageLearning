import { gql } from "graphql-request";
import { lessonPageFragment } from "../../lessonBook/gql/fragments";
export const chapterGenerationProgressQuery = gql`
    ${lessonPageFragment}
    subscription ChapterGenerationProgress($taskId: ID!) {
        chapterGenerationProgress(taskId: $taskId) {
            taskId
            progress
            message
            isComplete
            data {
                __typename
                ...on LessonPage {
                    ...LessonPageFragment
                }
            }
            error
        }
    }
`;