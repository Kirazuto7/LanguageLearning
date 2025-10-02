import { useEffect, useRef } from "react";
import { useAppSelector } from "../../app/hooks";
import { startSubscription, stopSubscription } from "../../app/services/subscriptionManager";
import { chapterGenerationProgressQuery } from "../../features/lessonBook/gql/queries";
import { shortStoryGenerationProgressQuery } from "../../features/storyBook/gql/queries";
import { store } from "../../app/store";
import { updateProgress } from "../../widgets/progressBar/progressSlice";
import {isLessonPageDTO, isStoryPageDTO, ProgressUpdateDTO} from "../../shared/types/dto";
import {logToServer, safeToString} from "../../shared/utils/loggingService";
import { lessonBookApiSlice } from "../../shared/api/lessonBookApiSlice";
import { storyBookApiSlice } from "../../shared/api/storyBookApiSlice";
import {GenerationType} from "../../shared/types/types";
import {selectCurrentUser} from "../../features/authentication/authSlice";

/**
 * A component that runs in the background to manage WebSocker subscriptions
 * for in-progress generation tasks. On page load, it monitors the Redux progress state
 * and automatically subscribes/unsubscribes to tasks as they are added, completed, or removed.
 */
const ProgressSubscriptionManager: React.FC = () => {
    const progressState = useAppSelector(state => state.progress);
    const currentUser = useAppSelector(selectCurrentUser);
    const subscribedTasksRef = useRef<Set<string>>(new Set());

    useEffect(() => {
        // When the user logs out, clean up all active subscriptions
        if (!currentUser) {
            subscribedTasksRef.current.forEach(taskId => {
                stopSubscription(taskId);
            });
            subscribedTasksRef.current.clear();
            return;
        }

        const activeTaskIdsInState = new Set(Object.keys(progressState));

        // Subscribe to new, relevant tasks
        Object.values(progressState).forEach(task => {
            if (!task?.progressData) return;

            const { taskId, isComplete, error } = task.progressData;

            // Subscribe if task is for the current user, is ongoing, and isn't already subscribed to.
            if (task.userId === currentUser.id && !isComplete && !error && !subscribedTasksRef.current.has(taskId)) {
                const { parentId, language, difficulty, generationType } = task;
                logToServer('info', `ProgressSubscriptionManager: New task found (${taskId}, type: ${generationType}). Starting subscription.`);

                const query = generationType === GenerationType.CHAPTER ? chapterGenerationProgressQuery : shortStoryGenerationProgressQuery;

                startSubscription<{ [key: string]: ProgressUpdateDTO }>(taskId, {
                    query,
                    variables: { taskId },
                    onNext: (progressData) => {
                        const update = progressData[Object.keys(progressData)[0]];
                        if (!update) return;

                        store.dispatch(updateProgress(update));

                        if (update.data && isLessonPageDTO(update.data)) {
                            const newPage = update.data;
                            store.dispatch(lessonBookApiSlice.util.updateQueryData("getLessonBook", { language, difficulty }, (draft) => {
                                const lessonChapter = draft.lessonChapters.find((c) => c.id === parentId);
                                if (lessonChapter && !lessonChapter.lessonPages.some((p) => p.id === newPage.id)) {
                                    lessonChapter.lessonPages.push(newPage);
                                    lessonChapter.lessonPages.sort((a, b) => parseInt(a.id, 10) - parseInt(b.id, 10));
                                }
                            }));
                        }
                        else if (update.data && isStoryPageDTO(update.data)) {
                            const newPage = update.data;
                            store.dispatch(storyBookApiSlice.util.updateQueryData("getStoryBook", { language, difficulty }, (draft) => {
                                const shortStory = draft.shortStories.find((s) => s.id === parentId);
                                if (shortStory && !shortStory.storyPages.some((p) => p.id === newPage.id)) {
                                    shortStory.storyPages.push(newPage);
                                    shortStory.storyPages.sort((a, b) => parseInt(a.id, 10) - parseInt(b.id, 10));
                                }
                            }));
                        }
                    },
                    onError: (error) => {
                        const errorMessage = error?.message || 'Subscription failed';
                        store.dispatch(updateProgress({ taskId, error: errorMessage, isComplete: false, progress: 0, message: 'Error' }));
                    },
                    onComplete: () => {
                        logToServer('info', `ProgressSubscriptionManager: Subscription complete for task ${taskId}. Triggering refetch.`);
                        if (generationType === GenerationType.CHAPTER) {
                            store.dispatch(lessonBookApiSlice.endpoints.getLessonBook.initiate({ language, difficulty }, { forceRefetch: true}));
                        }
                        else if (generationType === GenerationType.STORY) {
                            store.dispatch(storyBookApiSlice.endpoints.getStoryBook.initiate({ language, difficulty }, { forceRefetch: true}));
                        }
                    }
                });
                subscribedTasksRef.current.add(taskId);
            }
        });

        // Unsubscribed from tasks that are no longer active or present in the state
        subscribedTasksRef.current.forEach(subscribedTaskId => {
            const taskInState = progressState[subscribedTaskId];
            const isTaskGone = !taskInState;
            const isTaskCompleteOrErrored = taskInState?.progressData?.isComplete || !!taskInState?.progressData?.error;

            if (isTaskGone || isTaskCompleteOrErrored) {
                logToServer('info', `ProgressSubscriptionManager: Task ${subscribedTaskId} is complete, error, or removed. Stopping subscription.`);
                stopSubscription(subscribedTaskId);
                subscribedTasksRef.current.delete(subscribedTaskId);
            }
        });

    }, [progressState, currentUser]);

    return null; // No render
};

export default ProgressSubscriptionManager;