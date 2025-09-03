import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import { PracticeLessonCheckRequest, PracticeLessonCheckResponse } from "../../types/dto";

export const practiceLessonApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        // Mutation to proofread a Practice Lesson Sentence
        proofread: builder.mutation<PracticeLessonCheckResponse, PracticeLessonCheckRequest>({
            query: (request) => ({
                body: gql`
                    mutation CheckPracticeSentence($request: PracticeLessonCheckRequestInput!) {
                        checkPracticeSentence(request: $request) {
                            isCorrect
                            correctedSentence
                            feedback
                        }
                    }
                `,
                variables: { request },
            }),
            transformResponse: (response: { checkPracticeSentence: PracticeLessonCheckResponse }) => response.checkPracticeSentence,
        }),

    })
});

export const { useProofreadMutation } = practiceLessonApiSlice;