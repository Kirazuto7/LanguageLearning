import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import {ChapterDTO, ChapterGenerationRequest, isPageDTO, ProgressUpdateDTO} from "../../types/dto";
import { lessonBookApiSlice } from "./lessonBookApiSlice";
import {logToServer, toString} from "../../utils/loggingService";
import { chapterFragment } from "../gqlQueries/queryFragments";
import { startSubscription } from "../../managers/subscriptionManager";
import { chapterGenerationProgressQuery } from "../gqlQueries/queryExports";
import { clearProgress, updateProgress } from "../state/progressSlice";
import { store } from "../../app/store";

export const chapterApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        // Query to fetch a Chapter by id
        getChapter: builder.query<ChapterDTO, string>({
            query:(id) => ({
                body: gql`
                    ${chapterFragment}
                    query GetChapter($id: ID!) {
                        getChapterById(id: $id) {
                            ...ChapterFragment
                        }
                    }
                `,
                variables: { id },
            }),
            transformResponse: (response: { getChapterById: ChapterDTO }) => response.getChapterById,
            providesTags: (result) => (result ? [{ type: 'Chapter', id: result.id }] : []),
        }),

        // Mutation to generate a new Chapter
        generateChapter: builder.mutation<{ taskId: string; chapter: ChapterDTO }, ChapterGenerationRequest>({
            query: ({ language, difficulty, topic }) => ({
                body: gql`
                    mutation GenerateChapter($request: ChapterGenerationRequestInput!) {
                        generateChapter(request: $request) {
                            taskId
                            chapter {
                                id
                                chapterNumber
                                title
                                nativeTitle
                                pages { id } # Initially empty
                            }
                        }
                    }
               `,
                variables: { request: { language, difficulty, topic } },
            }),
            transformResponse: (response: { generateChapter: { taskId: string; chapter: ChapterDTO } }) => response.generateChapter,
            async onQueryStarted({ language, difficulty }, { dispatch, queryFulfilled }) {
                try {
                    const { data } = await queryFulfilled;
                    const { taskId, chapter: newChapter } = data;

                    // 1. Adds the chapter shell to the lesson book
                    dispatch(
                        lessonBookApiSlice.util.updateQueryData('getLessonBook', { language, difficulty }, (draft) => {
                            draft.chapters.push(newChapter);
                        })
                    );

                    // 2. Start the subscription to get progress & page updates for the chapter
                    startSubscription<{ chapterGenerationProgress: ProgressUpdateDTO; }>(taskId, {
                        query: chapterGenerationProgressQuery,
                        variables: { taskId },
                        onNext: (progressData) => {
                            const update = progressData.chapterGenerationProgress;
                            if (!update) return;

                            logToServer("info", "Subscription Update:", toString(update));

                            // Dispatch the raw progress update to the progress slice
                            store.dispatch(updateProgress(update));

                            // If the update contains a new page, patch the main lesson book cache
                            if (update.data && isPageDTO(update.data)) {
                                const newPage = update.data;
                                store.dispatch(
                                    lessonBookApiSlice.util.updateQueryData(
                                        "getLessonBook",
                                        { language, difficulty },
                                        (draft) => {
                                            const chapter = draft.chapters.find((c) => c.id === newChapter.id);
                                            if (chapter && !chapter.pages.some((p) => p.id === newPage.id)) {
                                                chapter.pages.push(newPage);
                                                chapter.pages.sort((a, b) => a.pageNumber - b.pageNumber);
                                            }
                                        }
                                    )
                                );
                            }
                        },
                        onComplete: () => {
                            // Force a refetch to ensure data integrity between the client & server.
                            dispatch(lessonBookApiSlice.endpoints.getLessonBook.initiate({ language, difficulty }, { forceRefetch: true }));

                            setTimeout(() => {
                                store.dispatch(clearProgress(taskId));
                            }, 5000);
                        }
                    });
                }
                catch (err) {
                    logToServer('error', "Failed to start chapter generation:", { error: err });
                }
            }
        }),

    })
});

export const { useGetChapterQuery, useLazyGetChapterQuery, useGenerateChapterMutation } = chapterApiSlice;