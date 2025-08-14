import { apiSlice } from "../api/apiSlice";
import { LessonBookDTO, ChapterDTO } from "../../types/dto";

export const bookApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        // Initial Book Data
        fetchBook: builder.query<LessonBookDTO, { language: string, difficulty: string}>({
            query: credentials => ({
                url: '/books/fetch/book',
                method: 'POST',
                body: credentials
            }),
            providesTags: (result, error, { language, difficulty }) => [{ type: 'Book', id: `${language}-${difficulty}`}],
        }),

        // Generate a New Chapter
        generateChapter: builder.mutation<ChapterDTO, { language: string, difficulty: string, topic: string, userId: number}>({
            query: ({language, difficulty, topic, userId}) => ({ // Book Id not part of request body
                url: '/chapters/generate',
                method: 'POST',
                body: {language, difficulty, topic, userId}
            }),
            invalidatesTags: (result, error, {language, difficulty}, meta) => [{ type: 'Book', id: `${language}-${difficulty}` }],
        }),
    })
});

export const { useFetchBookQuery, useGenerateChapterMutation } = bookApiSlice;