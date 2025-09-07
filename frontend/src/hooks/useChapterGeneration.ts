import { useState, useEffect, useCallback } from "react";
import { useGenerateChapterMutation, useChapterGenerationProgressQuery } from "../features/api/chapterApiSlice";

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
    const [generateChapter, { isLoading: isMutationLoading, error: mutationError }] = useGenerateChapterMutation();
    const { data: progressData, error: subscriptionError, isFetching: isQueryFetching } = useChapterGenerationProgressQuery(
        { taskId: taskId!, language, difficulty },
        { skip: !taskId }
    );

    // Reset state on component unmount
    useEffect(() => {
        return () => {
            setTaskId(null);
        };
    }, []);

    // Reset the taskId when the language or difficulty changes
    useEffect(() => {
        if (taskId) return; // Don't reset in the middle of generation
        setTaskId(null);
    }, [language, difficulty, taskId]);

    // Triggered when the generation is complete or fails to reset
    useEffect(() => {
        const progress = progressData?.chapterGenerationProgress;
        if (progress?.isComplete || progress?.error) {
            setTaskId(null);
        }
    }, [progressData?.chapterGenerationProgress?.isComplete, progressData?.chapterGenerationProgress?.error]);

    const startGeneration = useCallback(async (topic: string) => {
        if (taskId) return; // Prevent starting a new generation if one is active

        try {
            const { taskId: newTaskId } = await generateChapter({ language, difficulty, topic }).unwrap();
            setTaskId(newTaskId); // Trigger the subscription query to begin
        }
        catch (err) {
            console.error('Failed to start chapter generation:', err);
            setTaskId(null);
        }
    }, [generateChapter, language, difficulty, taskId]);

    const progress = progressData?.chapterGenerationProgress;
    const progressValue = progress?.progress;
    const progressMessage = progress?.message;
    const isComplete = progress?.isComplete && !isQueryFetching;
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