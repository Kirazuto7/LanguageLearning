import { gql } from "graphql-request";

export const translationFragment = gql`
    fragment TranslationFragment on TranslationResponse {
        translatedText
    }
`;