import { useState, useEffect, useCallback } from "react";
import { useAppDispatch } from "../app/hooks";
import { useGenerateChapterMutation, useChapterGenerationProgressQuery } from "../features/api/chapterApiSlice";
import { lessonBookApiSlice } from "../features/api/lessonBookApiSlice";
import { logToServer, toString } from "../utils/loggingService";

/**
 * A custom hook to manage the entire chapter generation workflow.
 * It orchestrates the mutation to start the generation, the optimistic UI update,
 * and the real-time cache updates from the progress subscription.
 *
 * @param language The language of the book being updated.
 * @param difficulty The difficulty of the book being updated.
 */
export const useChapterGeneration = (language: string, difficulty: string) => {
    const [taskId, setTaskId] = useState<string | null>(null);
    const dispatch = useAppDispatch();
    const [generateChapter, { isLoading: isMutationLoading, error: mutationError }] = useGenerateChapterMutation();
    const { data: progressData, error: subscriptionError } = useChapterGenerationProgressQuery(
        { taskId: taskId! },
        { skip: !taskId }
    );

    // Listens for new pages retrieved from the subscription and patches the main book cache.
    useEffect(() => {
        const update = progressData?.chapterGenerationProgress;
        //console.log("Update: \n" + toString(update) + "\n");
        //logToServer('info', "Progress Data:", toString(progressData));
        if (update && update.data && update.chapterId) {
            const newPage  = update.data;
            const chapterIdToUpdate = update.chapterId;
            //console.log("Page Data: " + JSON.stringify(update.data, null, 2) + "\n");

            dispatch(
                lessonBookApiSlice.util.updateQueryData(
                    'getLessonBook',
                    { language, difficulty },
                    (draft) => {
                        const chapter = draft.chapters.find(c => c.id === String(chapterIdToUpdate));
                        if (chapter) {
                            // Check to avoid adding a page that is already in the 'pages' array
                            if (!chapter.pages.some(p => p.id === newPage.id)) {
                                chapter.pages.push(newPage);
                            }
                        }
                    }
                )
            );
        }

    }, [progressData, dispatch, language, difficulty]);


    const startGeneration = useCallback(async (topic: string) => {
        setTaskId(null);
        try {
            const { taskId: newTaskId } = await generateChapter({ language, difficulty, topic }).unwrap();
            setTaskId(newTaskId); // Trigger the subscription query to begin
        }
        catch (err) {
            console.error('Failed to start chapter generation:', err);
        }
    }, [generateChapter, language, difficulty]);

    const progress = progressData?.chapterGenerationProgress;
    const progressValue = progress?.progress;
    const progressMessage = progress?.message;
    const isComplete = progress?.progress === 100;
    const generationError = mutationError || subscriptionError || progress?.error;

    const isLoading = isMutationLoading || (!!taskId && !isComplete && !generationError);

    return {
        startGeneration,
        isLoading,
        progress: progressValue ?? 0,
        message: progressMessage ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? 'Chapter generation failed.' : null,
        isComplete: isComplete,
    };
};