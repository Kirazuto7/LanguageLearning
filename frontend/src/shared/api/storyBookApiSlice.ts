import { graphqlApiSlice } from "./graphqlApiSlice";
import {StoryBookDTO, StoryBookRequest} from "../types/dto";
import {getStoryBook, getStoryBooks} from "../../features/storyBook/gql/queries";

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
            query: ({ language, difficulty }) => ({
                body: getStoryBook,
                variables: { language, difficulty },
            }),
            transformResponse: (response: { getStoryBook: StoryBookDTO }) => response.getStoryBook,
            providesTags: (result) => (result ? [{ type: 'Book', id: String(result.id) }] : []),
        }),

    })
});

export const { useGetStoryBooksQuery, useGetStoryBookQuery } = storyBookApiSlice;