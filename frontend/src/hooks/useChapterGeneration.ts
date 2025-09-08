import { useState, useEffect, useCallback } from "react";
import { useGenerateChapterMutation } from "../features/api/chapterApiSlice";
import { useAppSelector } from "../app/hooks";
import { selectProgressByTaskId } from "../features/state/progressSlice";

/**
 * A custom hook to manage the entire chapter generation workflow.
 * It orchestrates the mutation to start the generation and reads real-time
 * progress from the global state, which is managed by the subscriptionManager.
 *
 * @param language The language of the book being updated.
 * @param difficulty The difficulty of the book being updated.
 */
export const useChapterGeneration = (language: string, difficulty: string) => {
    const [taskId, setTaskId] = useState<string | null>(null);
    const [generateChapter, { isLoading: isMutationLoading, error: mutationError }] = useGenerateChapterMutation();

    const progress = useAppSelector(state => taskId ? selectProgressByTaskId(state, taskId) : undefined);

    // Reset state on component unmount
    useEffect(() => {
        return () => {
            setTaskId(null);
        };
    }, []);

    // If the language/difficulty changes, the lesson book instance changes as well.
    // Therefore, we reset the state for tracking the new context.
    useEffect(() => {
        if (taskId) return;
        setTaskId(null);
    }, [language, difficulty]);

    // Triggered when the generation is complete or fails to reset after a short delay
    // to allow the UI to show the final "complete" or "error" state.
    useEffect(() => {
        if (progress?.isComplete || progress?.error) {
            const timer = setTimeout(() => {
                setTaskId(null);
            }, 5000);
            return () => {
                clearTimeout(timer);
            }
        }
    }, [progress?.isComplete, progress?.error]);

    const startGeneration = useCallback(async (topic: string) => {
        if (isMutationLoading || taskId) return; // Prevent starting a new generation if one is active

        try {
            const { taskId: newTaskId } = await generateChapter({ language, difficulty, topic }).unwrap();
            // By setting the taskId, we trigger the subscription query to begin tracking progress updates.
            setTaskId(newTaskId);
        }
        catch (err) {
            console.error('Failed to start chapter generation:', err);
            setTaskId(null);
        }
    }, [generateChapter, language, difficulty, taskId, isMutationLoading]);

    const isComplete = !!progress?.isComplete;
    const generationError = mutationError || progress?.error;
    const isLoading = isMutationLoading || (!!taskId && !isComplete && !generationError);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? 'Chapter generation failed.' : null,
        isComplete: isComplete,
    };
};