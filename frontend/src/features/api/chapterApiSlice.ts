import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import {ChapterDTO, ChapterGenerationRequest, ProgressUpdateDTO, LessonBookRequest } from "../../types/dto";
import { lessonBookApiSlice } from "./lessonBookApiSlice";
import { logToServer, toString } from "../../utils/loggingService";
import { subscribe } from "../clients/wsClient";
import { chapterGenerationProgressQuery } from "../gqlQueries/queryExports";
import { chapterFragment } from "../gqlQueries/queryFragments";


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
            { taskId: string }
        >({
            queryFn: () => ({ data: { chapterGenerationProgress: undefined } }),
            async onCacheEntryAdded({ taskId }, { updateCachedData, cacheEntryRemoved, cacheDataLoaded }) {
                await cacheDataLoaded;
                let unsubscribe: () => void;
                const wsClosedPromise = new Promise<void>(resolve => {
                    unsubscribe = subscribe<{ chapterGenerationProgress: ProgressUpdateDTO; }>(
                        chapterGenerationProgressQuery,
                        { taskId },
                        (progressData) => {
                            updateCachedData((draft) => {
                                logToServer('info', "Incoming Data:", toString(progressData));
                                Object.assign(draft, progressData);
                            });
                            if (progressData.chapterGenerationProgress?.progress === 100 || progressData.chapterGenerationProgress?.error) {
                                resolve();
                            }
                        },
                        (err) => {
                            logToServer('error', "WS error", { error: err });
                            resolve();
                        },
                        () => {
                            logToServer('info', "WS complete", null);
                            resolve();
                        }
                    );
                });

                await Promise.race([cacheEntryRemoved, wsClosedPromise]);
                unsubscribe!();
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