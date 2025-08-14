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
            })
        }),

        // Generate a New Chapter
        generateChapter: builder.mutation<ChapterDTO, { language: string, difficulty: string, topic: string, userId: number, bookId: number}>({
            query: ({language, difficulty, topic, userId}) => ({ // Book Id not part of request body
                url: '/chapters/generate',
                method: 'POST',
                body: {language, difficulty, topic, userId}
            }),
            invalidatesTags: (result, error, arg, meta) => [{ type: 'Book', id: arg.bookId }],
        }),
    })
});

export const { useFetchBookQuery, useGenerateChapterMutation } = bookApiSlice;