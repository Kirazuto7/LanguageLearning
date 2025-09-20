import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import {LessonChapterDTO, ChapterGenerationRequest, isLessonPageDTO, ProgressUpdateDTO} from "../types/dto";
import { lessonBookApiSlice } from "./lessonBookApiSlice";
import {logToServer, toString} from "../utils/loggingService";
import { lessonChapterFragment } from "../../features/lessonBook/gql/fragments";
import { startSubscription } from "../../app/services/subscriptionManager";
import { chapterGenerationProgressQuery } from "../../features/chapterGeneration/gql/queries";
import { clearProgress, updateProgress } from "../../widgets/progressBar/progressSlice";
import { store } from "../../app/store";

export const chapterApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        // Query to fetch a Chapter by id
        getChapter: builder.query<LessonChapterDTO, string>({
            query:(id) => ({
                body: gql`
                    ${lessonChapterFragment}
                    query GetChapter($id: ID!) {
                        getChapterById(id: $id) {
                            ...LessonChapterFragment
                        }
                    }
                `,
                variables: { id },
            }),
            transformResponse: (response: { getChapterById: LessonChapterDTO }) => response.getChapterById,
            providesTags: (result) => (result ? [{ type: 'Chapter', id: result.id }] : []),
        }),

        // Mutation to generate a new Chapter
        generateChapter: builder.mutation<{ taskId: string; lessonChapter: LessonChapterDTO }, ChapterGenerationRequest>({
            query: ({ language, difficulty, topic }) => ({
                body: gql`
                    mutation GenerateChapter($request: ChapterGenerationRequestInput!) {
                        generateChapter(request: $request) {
                            taskId
                            lessonChapter {
                                id
                                chapterNumber
                                title
                                nativeTitle
                                lessonPages { id } # Initially empty
                            }
                        }
                    }
               `,
                variables: { request: { language, difficulty, topic } },
            }),
            transformResponse: (response: { generateChapter: { taskId: string; lessonChapter: LessonChapterDTO } }) => response.generateChapter,
            async onQueryStarted({ language, difficulty }, { dispatch, queryFulfilled }) {
                try {
                    const { data } = await queryFulfilled;
                    const { taskId, lessonChapter: newLessonChapter } = data;

                    // 1. Adds the lessonChapter shell to the lesson book
                    dispatch(
                        lessonBookApiSlice.util.updateQueryData('getLessonBook', { language, difficulty }, (draft) => {
                            draft.lessonChapters.push(newLessonChapter);
                        })
                    );

                    // 2. Start the subscription to get progress & lessonPage updates for the lessonChapter
                    startSubscription<{ chapterGenerationProgress: ProgressUpdateDTO; }>(taskId, {
                        query: chapterGenerationProgressQuery,
                        variables: { taskId },
                        onNext: (progressData) => {
                            const update = progressData.chapterGenerationProgress;
                            if (!update) return;

                            logToServer("info", "Subscription Update:", toString(update));

                            // Dispatch the raw progress update to the progress slice
                            store.dispatch(updateProgress(update));

                            // If the update contains a new lessonPage, patch the main lesson book cache
                            if (update.data && isLessonPageDTO(update.data)) {
                                const newPage = update.data;
                                store.dispatch(
                                    lessonBookApiSlice.util.updateQueryData(
                                        "getLessonBook",
                                        { language, difficulty },
                                        (draft) => {
                                            const lessonChapter = draft.lessonChapters.find((c) => c.id === newLessonChapter.id);
                                            if (lessonChapter && !lessonChapter.lessonPages.some((p) => p.id === newPage.id)) {
                                                lessonChapter.lessonPages.push(newPage);
                                                lessonChapter.lessonPages.sort((a, b) => a.pageNumber - b.pageNumber);
                                            }
                                        }
                                    )
                                );
                            }
                        },
                        onError: (error) => {
                            const errorMessage = error?.message || 'Subscription failed';
                            dispatch(updateProgress({ taskId, error: errorMessage, isComplete: false, progress: 0, message: 'Error' }));
                        },
                        onComplete: () => {
                            // Force a refetch to ensure data integrity between the client & server.
                            dispatch(lessonBookApiSlice.endpoints.getLessonBook.initiate({ language, difficulty }, { forceRefetch: true }));
                        }
                    });
                }
                catch (err) {
                    logToServer('error', "Failed to start lessonChapter generation:", { error: err });
                }
            }
        }),

    })
});

export const { useGetChapterQuery, useLazyGetChapterQuery, useGenerateChapterMutation } = chapterApiSlice;