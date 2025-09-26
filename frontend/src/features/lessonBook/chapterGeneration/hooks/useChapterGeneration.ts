import { useState, useEffect, useCallback } from "react";
import { useGenerateChapterMutation } from "../../../../shared/api/chapterApiSlice";
import { useAppSelector, useAppDispatch } from "../../../../app/hooks";
import { selectProgressByTaskId, startGenerationTracking, selectActiveTaskIdForContext, markProgressAsStale } from "../../../../widgets/progressBar/progressSlice";

/**
 * A custom hook to manage the entire lessonChapter generation workflow.
 * It orchestrates the mutation to start the generation and reads real-time
 * progress from the global state, which is managed by the subscriptionManager.
 *
 * @param language The language of the book being updated.
 * @param difficulty The difficulty of the book being updated.
 */
export const useChapterGeneration = (language: string, difficulty: string) => {
    const dispatch = useAppDispatch();
    const [taskId, setTaskId] = useState<string | null>(null);
    const [generateChapter, { isLoading: isMutationLoading, error: mutationError }] = useGenerateChapterMutation();

    // Check if there is an active task in the global state
    const activeTaskId = useAppSelector(state => selectActiveTaskIdForContext(state, { language, difficulty }));
    useEffect(() => {
        if (activeTaskId) {
            setTaskId(activeTaskId);
        }
    }, [activeTaskId]);


    const progress = useAppSelector(state => taskId ? selectProgressByTaskId(state, taskId) : undefined);
    const wasClearedPrematurely = !!taskId && !progress;

    // If the language/difficulty changes, reset the local taskId.
    useEffect(() => {
        setTaskId(null);
    }, [language, difficulty]);

    // Triggered when the generation is complete or fails to reset after a short delay
    // to allow the UI to show the final "complete" or "error" state.
    useEffect(() => {
        if (progress?.isComplete || progress?.error || wasClearedPrematurely) {
            const timer = setTimeout(() => {
                if (taskId) {
                    dispatch(markProgressAsStale(taskId));
                }
                setTaskId(null);
            }, 5000);
            return () => {
                clearTimeout(timer);
            }
        }
    }, [progress?.isComplete, progress?.error, wasClearedPrematurely, taskId, dispatch]);

    const startGeneration = useCallback(async (topic: string) => {
        if (isMutationLoading || taskId) return; // Prevent starting a new generation if one is active

        try {
            const { taskId: newTaskId } = await generateChapter({ language, difficulty, topic }).unwrap();
            dispatch(startGenerationTracking({ taskId: newTaskId, language, difficulty }));
            // By setting the taskId, we trigger the subscription query to begin tracking progress updates.
            setTaskId(newTaskId);
        }
        catch (err) {
            console.error('Failed to start lessonChapter generation:', err);
            setTaskId(null);
        }
    }, [generateChapter, language, difficulty, taskId, isMutationLoading, dispatch]);

    const isComplete = !!progress?.isComplete;
    const generationError = mutationError || progress?.error || (wasClearedPrematurely ? 'Connection to the server was lost.' : undefined);
    const isLoading = isMutationLoading || (!!taskId && !isComplete && !generationError && !wasClearedPrematurely);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? `Chapter generation failed: ${generationError}` : null,
        isComplete: isComplete,
    };
};