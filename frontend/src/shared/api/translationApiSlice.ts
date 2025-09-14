import { graphqlApiSlice } from "./graphqlApiSlice";
import { TranslationRequest, TranslationResponse } from "../types/dto";
import {gql} from "graphql-request";
import { translationFragment } from "../../widgets/translationTool/gql/fragments";

export const translationApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        translate: builder.mutation<TranslationResponse, TranslationRequest>({
            query: (request) => ({
                body: gql`
                    ${translationFragment}
                    mutation TranslateText($request: TranslationRequest!) {
                        translateText(request: $request) {
                            ...TranslationFragment
                        }
                    }
                `,
                variables: {request},
            }),
            transformResponse: (response: { translateText: TranslationResponse }) => response.translateText,
        }),

    })
});

export const { useTranslateMutation } = translationApiSlice;