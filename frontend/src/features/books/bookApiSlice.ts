import { apiSlice } from "../api/apiSlice";
import { LessonBookDTO, ChapterDTO, LessonBookRequest, ChapterGenerationRequest } from "../../types/dto";

export const bookApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        // Initial Book Data
        fetchBook: builder.query<LessonBookDTO, LessonBookRequest>({
            query: credentials => ({
                url: '/books/fetch/book',
                method: 'POST',
                body: credentials
            }),
            providesTags: (result, error, { language, difficulty, userId }) => [{ type: 'Book', id: `${language}-${difficulty}-${userId}`}],
        }),

        // Generate a New Chapter
        generateChapter: builder.mutation<ChapterDTO, ChapterGenerationRequest>({
            query: (request) => ({
                url: '/chapters/generate',
                method: 'POST',
                body: request
            }),
            invalidatesTags: (result, error, {language, difficulty, userId}, meta) => [{ type: 'Book', id: `${language}-${difficulty}-${userId}` }],
        }),
    })
});

export const { useFetchBookQuery, useGenerateChapterMutation } = bookApiSlice;