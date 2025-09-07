import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import {ChapterDTO, ChapterGenerationRequest, ProgressUpdateDTO, LessonBookRequest } from "../../types/dto";
import { lessonBookApiSlice } from "./lessonBookApiSlice";
import { logToServer, toString } from "../../utils/loggingService";
import { subscribe } from "../clients/wsClient";
import { chapterGenerationProgressQuery } from "../gqlQueries/queryExports";
import { chapterFragment } from "../gqlQueries/queryFragments";
import {Client, createClient} from "graphql-ws";


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

        chapterGenerationProgress: builder.query<
            { chapterGenerationProgress?: ProgressUpdateDTO },
            { taskId: string, language: string, difficulty: string }
        >({
            queryFn: () => ({ data: { chapterGenerationProgress: undefined } }),
            serializeQueryArgs: ({ queryArgs }) => {
                //return queryArgs.taskId;
                return `${queryArgs.taskId}-${queryArgs.language}-${queryArgs.difficulty}`;
            },
            keepUnusedDataFor: 3600,
            async onCacheEntryAdded({ taskId, language, difficulty }, { updateCachedData, cacheEntryRemoved, cacheDataLoaded, dispatch }) {
                try {
                    await cacheDataLoaded;

                    let unsubscribe: (() => void) | undefined;
                    /*const wsProtocol = window.location.protocol === "https:" ? "wss:" : "ws:";
                    const wsClient: Client = createClient({
                        url: `${wsProtocol}//${window.location.host}/graphql`,
                    });*/

                    // Create a promise that resolves when the WebSocket stream is completed or errors out.
                    const wsCompletionPromise = new Promise<void>((resolve) => {
                        unsubscribe = subscribe<{ chapterGenerationProgress: ProgressUpdateDTO; }>(
                            chapterGenerationProgressQuery,
                            { taskId },
                            (progressData) => { // onNext
                                updateCachedData((draft) => {
                                    Object.assign(draft, progressData);
                                });

                                // If the update contains a new page, patch the main lesson book cache
                                const update = progressData.chapterGenerationProgress;
                                logToServer('info', "Incoming Data:", toString(progressData));
                                if (update && update.data && update.chapterId) {
                                    const newPage = update.data;
                                    dispatch(
                                        lessonBookApiSlice.util.updateQueryData(
                                            'getLessonBook',
                                            { language, difficulty },
                                            (draft) => {
                                                const chapter = draft.chapters.find(c => c.id === String(update.chapterId));
                                                if (chapter && !chapter.pages.some(p => p.id === newPage.id)) {
                                                    chapter.pages.push(newPage);
                                                }
                                            }
                                        )
                                    );
                                }
                            },
                            (err) => { // onError
                                logToServer('error', "WS error", { error: err, taskId });
                                resolve(); // Resolve the promise on error to trigger cleanup
                            },
                            () => { // onComplete
                                logToServer('info', "WS stream from server has completed.", { taskId });
                                resolve(); // Resolve the promise on completion to trigger cleanup
                            }
                        );
                    });

                    // Wait for either the WebSocket to complete or the cache entry to be removed.
                    await Promise.race([cacheEntryRemoved, wsCompletionPromise]);

                    // Unsubscribe from the WebSocket connection if it's still active.
                    if (unsubscribe) {
                        unsubscribe();
                        logToServer('info', "Unsubscribed due to cache entry removal or WS completion.", { taskId });
                    }
                } catch {
                    // The cache entry was removed before the subscription could be established.
                    // This is a normal part of the lifecycle and requires no action.
                }
            }
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
                    const { data: { chapter: newChapter } } = await queryFulfilled;

                    dispatch(
                        lessonBookApiSlice.util.updateQueryData('getLessonBook', { language, difficulty }, (draft) => {
                            draft.chapters.push(newChapter);
                        })
                    );
                }
                catch (err) {
                    logToServer('error', "Failed to start chapter generation:", { error: err });
                }
            }
        }),

    })
});

export const { useGetChapterQuery, useLazyGetChapterQuery, useLazyChapterGenerationProgressQuery, useChapterGenerationProgressQuery, useGenerateChapterMutation } = chapterApiSlice;