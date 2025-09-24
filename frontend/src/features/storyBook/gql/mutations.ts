import {gql} from "graphql-request";
import {shortStoryFragment} from "./fragments";


export const generateShortStory = gql`
    ${shortStoryFragment}
    mutation GenerateShortStory($request: ShortStoryGenerationRequestInput!) {
        generateShortStory(request: $request) {
            taskId
            shortStory {
                ...ShortStoryFragment
            }
        }
    }
`;