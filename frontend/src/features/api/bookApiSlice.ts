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
        generateChapter: builder.mutation<{taskId: string }, ChapterGenerationRequest>({
            query: (request) => ({
                url: '/chapters/generate',
                method: 'POST',
                body: request
            }),
        }),
    })
});

export const { useFetchBookQuery, useGenerateChapterMutation } = bookApiSlice;