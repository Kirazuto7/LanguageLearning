import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import { PracticeLessonCheckRequest, PracticeLessonCheckResponse } from "../types/dto";

export const practiceLessonApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        // Mutation to proofread a Practice Lesson Sentence
        proofread: builder.mutation<PracticeLessonCheckResponse, PracticeLessonCheckRequest>({
            query: (request) => ({
                body: gql`
                    mutation ProofReadPracticeSentence($request: PracticeLessonCheckRequestInput!) {
                        proofreadPracticeSentence(request: $request) {
                            isCorrect
                            correctedSentence
                            feedback
                        }
                    }
                `,
                variables: { request },
            }),
            transformResponse: (response: { proofreadPracticeSentence: PracticeLessonCheckResponse }) => response.proofreadPracticeSentence,
        }),

    })
});

export const { useProofreadMutation } = practiceLessonApiSlice;