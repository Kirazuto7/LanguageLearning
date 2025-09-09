import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import { lessonBookFragment } from "../../features/lessonBook/gql/fragments";
import { LessonBookDTO, LessonBookRequest } from "../types/dto";

export const lessonBookApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({

        // Query to fetch all of the user's lesson books
        getLessonBooks: builder.query<LessonBookDTO[], void>({
            query: () => ({
                body: gql`
                    query GetLessonBooks {
                        getLessonBooks {
                            id
                            bookTitle
                            difficulty
                            language
                        }
                    }
                `
            }),
            transformResponse: (response: { getLessonBooks: LessonBookDTO[] }) => response.getLessonBooks,
            providesTags: (result = []) => [
                { type: 'Book', id: 'LIST' },
                ...result.map(({ id }) => ({ type: 'Book' as const, id: String(id) })),
            ],
        }),

        // Query to Fetch a Specific LessonBook based on language and difficulty
        getLessonBook: builder.query<LessonBookDTO, LessonBookRequest>({
            query: (request) => ({
                body: gql`
                    ${lessonBookFragment}
                    query GetLessonBook($request: LessonBookRequestInput!) {
                        getLessonBook(request: $request) {
                            ...LessonBookFragment
                        }
                    }
                `,
                variables: { request },
            }),
            transformResponse: (response: { getLessonBook: LessonBookDTO }) => response.getLessonBook,
            providesTags: (result) => (result ? [{ type: 'Book', id: String(result.id) }] : []),
        }),

    })
});

export const { useGetLessonBooksQuery, useGetLessonBookQuery } = lessonBookApiSlice;
