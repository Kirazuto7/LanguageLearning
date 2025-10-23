import {graphqlApiSlice} from "./graphqlApiSlice";
import {gql} from "graphql-request";
import {ChapterGenerationRequest, isLessonPageDTO, LessonChapterDTO, ProgressUpdateDTO} from "../types/dto";
import {lessonBookApiSlice} from "./lessonBookApiSlice";
import {logToServer, safeToString} from "../utils/loggingService";
import {lessonChapterFragment} from "../../features/lessonBook/gql/fragments";
import {startSubscription, stopSubscription} from "../../app/services/subscriptionManager";
import {chapterGenerationProgressQuery} from "../../features/lessonBook/gql/queries";
import {updateProgress} from "../../widgets/progressBar/progressSlice";

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
                logToServer('info', "onQueryStarted: Fired");

                try {
                    const { data } = await queryFulfilled;
                    logToServer('info', "onQueryStarted: queryFulfilled successful", { data });
                    const { taskId, lessonChapter: newLessonChapter } = data;

                    // 1. Adds the lessonChapter shell to the lesson book
                    logToServer('info', "onQueryStarted: Dispatching update with new chapter:", newLessonChapter);
                    dispatch(
                        lessonBookApiSlice.util.updateQueryData('getLessonBook', { language, difficulty }, (draft) => {
                            draft.lessonChapters.push(newLessonChapter);
                        })
                    );
                    logToServer('info', "onQueryStarted: Update complete. Starting subscription...");

                    // 2. Start the subscription to get progress & lessonPage updates for the lessonChapter
                    startSubscription<{ chapterGenerationProgress: ProgressUpdateDTO; }>(taskId, {
                        query: chapterGenerationProgressQuery,
                        variables: { taskId },
                        onNext: (progressData) => {
                            const update = progressData.chapterGenerationProgress;
                            if (!update) return;

                            logToServer("debug", "Subscription Update:", safeToString(update));

                            // Dispatch the raw progress update to the progress slice
                            dispatch(updateProgress(update));

                            // If the update contains a new lessonPage, patch the main lesson book cache
                            if (update.data && isLessonPageDTO(update.data)) {
                                const newPage = update.data;
                                dispatch(
                                    lessonBookApiSlice.util.updateQueryData(
                                        "getLessonBook",
                                        { language, difficulty },
                                        (draft) => {
                                            const lessonChapter = draft.lessonChapters.find((c) => c.id === newLessonChapter.id);
                                            if (lessonChapter && !lessonChapter.lessonPages.some((p) => p.id === newPage.id)) {
                                                lessonChapter.lessonPages.push(newPage);
                                                lessonChapter.lessonPages.sort((a, b) => parseInt(a.id, 10) - parseInt(b.id, 10));
                                            }
                                        }
                                    )
                                );
                            }

                            if (update.isComplete || update.isError) {
                                logToServer('info', `Task ${taskId} complete via WebSocket. Stopping subscription and refetching book.`);

                                stopSubscription(taskId);
                                // Force a refetch to ensure data integrity between the client & server.
                                dispatch(lessonBookApiSlice.endpoints.getLessonBook.initiate({ language, difficulty }, { forceRefetch: true }));
                            }
                        },
                        onError: (error) => {
                            const errorMessage = error?.message || 'An unexpected error has occurred.';
                            logToServer('error', errorMessage);
                            dispatch(updateProgress({ taskId, isError: true, isComplete: false, progress: 0, message: errorMessage }));
                            stopSubscription(taskId);
                        },
                        onComplete: () => {
                            logToServer('warn', `Subscription ${taskId} stream closed unexpectedly. Polling fallback will take over if needed.`);
                        }
                    });
                }
                catch (err) {
                    logToServer('error', "onQueryStarted: queryFulfilled REJECTED with error:", { error: err });
                }
            }
        }),

    })
});

export const { useGetChapterQuery, useLazyGetChapterQuery, useGenerateChapterMutation } = chapterApiSlice;