import { graphqlApiSlice } from "./graphqlApiSlice";
import {StoryBookDTO, StoryBookRequest} from "../types/dto";
import {getStoryBooks} from "../../features/storyBook/gql/queries";
import {gql} from "graphql-request";
import {storyBookFragment} from "../../features/storyBook/gql/fragments";

export const storyBookApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({

        // Query to fetch all the user's story books
        getStoryBooks: builder.query<StoryBookDTO[], void>({
            query: () => ({
                body: getStoryBooks
            }),
            transformResponse: (response: { getStoryBooks: StoryBookDTO[] }) => response.getStoryBooks,
            providesTags: (result = []) => [
                { type: 'Book', id: 'LIST' },
                ...result.map(({ id }) => ({ type: 'Book' as const, id: String(id) })),
            ],
        }),

        // Query to Fetch a Specific Storybook based on language and difficulty
        getStoryBook: builder.query<StoryBookDTO, StoryBookRequest>({
            query: (request) => ({
                body: gql`
                    ${storyBookFragment}
                    query GetStoryBook($request: StoryBookRequestInput!) {
                        getStoryBook(request: $request) {
                            ...StoryBookFragment
                        }
                    }
                `,
                variables: { request },
            }),
            transformResponse: (response: { getStoryBook: StoryBookDTO }) => response.getStoryBook,
            providesTags: (result) => (result ? [{ type: 'Book', id: String(result.id) }] : []),
        }),

        getStoryBookById: builder.query<StoryBookDTO, number>({
            query: (id) => ({
                body: gql`
                    ${storyBookFragment}
                    query GetStoryBookById($id: ID!) {
                        getStoryBookById(id: $id) {
                            ...StoryBookFragment
                        }
                    }
                `,
                variables: { id },
            }),
            transformResponse: (response: { getStoryBookById: StoryBookDTO }) => response.getStoryBookById,
            providesTags: (result) => (result ? [{ type: 'Book', id: String(result.id) }] : []),
        }),

    })
});

export const { useGetStoryBooksQuery, useGetStoryBookQuery, useGetStoryBookByIdQuery, useLazyGetStoryBookByIdQuery } = storyBookApiSlice;