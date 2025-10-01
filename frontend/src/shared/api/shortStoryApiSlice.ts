import {graphqlApiSlice} from "./graphqlApiSlice";
import {isStoryPageDTO, ProgressUpdateDTO, ShortStoryDTO, ShortStoryGenerationRequest} from "../types/dto";
import {generateShortStory} from "../../features/storyBook/gql/mutations";
import {logToServer, safeToString} from "../utils/loggingService";
import {storyBookApiSlice} from "./storyBookApiSlice";
import {startSubscription} from "../../app/services/subscriptionManager";
import {shortStoryGenerationProgressQuery} from "../../features/storyBook/gql/queries";
import {updateProgress} from "../../widgets/progressBar/progressSlice";
import {store} from "../../app/store";


export const shortStoryApiSlice = graphqlApiSlice.injectEndpoints ({
    endpoints: builder => ({

        generateShortStory: builder.mutation<{ taskId: string; shortStory: ShortStoryDTO }, ShortStoryGenerationRequest>({
            query: ({ language, difficulty, topic, genre }) => ({
                body: generateShortStory,
                variables: { request: { language, difficulty, topic, genre } },
            }),
            transformResponse: (response: { generateShortStory: { taskId: string; shortStory: ShortStoryDTO } }) => response.generateShortStory,
            async onQueryStarted({ language, difficulty }, { dispatch, queryFulfilled }) {
                logToServer('info', "onQueryStarted: Fired for generateShortStory");
                try {
                    const { data } = await queryFulfilled;
                    logToServer('info', "onQueryStarted: queryFulfilled successful", { data });
                    const { taskId, shortStory: newShortStory } = data;

                    // 1. Adds the shortStory shell to the story book
                    logToServer('info', "onQueryStarted: Dispatching optimistic update with new story:", newShortStory);
                    dispatch(
                        storyBookApiSlice.util.updateQueryData('getStoryBook', { language, difficulty }, (draft) => {
                            draft.shortStories.push(newShortStory);
                        })
                    );
                    logToServer('info', "onQueryStarted: Optimistic update complete. Starting subscription...");

                    // 2. Start the subscription to get progress & storyPage updates for the shortStory
                    startSubscription<{ shortStoryGenerationProgress: ProgressUpdateDTO; }>(taskId, {
                        query: shortStoryGenerationProgressQuery,
                        variables: { taskId },
                        onNext: (progressData) => {
                            const update = progressData.shortStoryGenerationProgress;
                            if (!update) return;

                            logToServer("debug", "Subscription Update:", safeToString(update));
                            store.dispatch(updateProgress(update));

                            if (update.data && isStoryPageDTO(update.data)) {
                                const newPage = update.data;
                                store.dispatch(
                                    storyBookApiSlice.util.updateQueryData("getStoryBook", { language, difficulty }, (draft) => {
                                        const shortStory = draft.shortStories.find((s) => s.id === newShortStory.id);
                                        if (shortStory && !shortStory.storyPages.some((p) => p.id === newPage.id)) {
                                            shortStory.storyPages.push(newPage);
                                            shortStory.storyPages.sort((a, b) => parseInt(a.id, 10) - parseInt(b.id, 10));
                                        }
                                    })
                                );
                            }
                        },
                        onError: (error) => {
                            const errorMessage = error?.message || 'Subscription failed';
                            dispatch(updateProgress({ taskId, error: errorMessage, isComplete: false, progress: 0, message: 'Error' }));
                        },
                        onComplete: () => {
                            dispatch(storyBookApiSlice.endpoints.getStoryBook.initiate({ language, difficulty }, { forceRefetch: true }));
                        }
                    });
                }
                catch (err) {
                    logToServer('error', "onQueryStarted: queryFulfilled REJECTED with error:", { error: err });
                }
            }
        })

    })
});

export const { useGenerateShortStoryMutation } = shortStoryApiSlice;