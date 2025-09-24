import {useAppDispatch, useAppSelector} from "../../../../app/hooks";
import {useCallback, useEffect, useState} from "react";
import {useGenerateShortStoryMutation} from "../../../../shared/api/shortStoryApiSlice";
import {
    clearProgress,
    selectActiveTaskIdForContext,
    selectProgressByTaskId, startGenerationTracking
} from "../../../../widgets/progressBar/progressSlice";

interface GenerationParams {
    topic?: string;
    genre?: string;
}

/**
 * A custom hook to manage the entire short story generation workflow.
 * It orchestrates the mutation to start the generation and reads real-time
 * progress from the global state, which is managed by the subscriptionManager.
 *
 * @param language The language of the story book being updated.
 * @param difficulty The difficulty of the story book being updated.
 */
export const useShortStoryGeneration = (language: string, difficulty: string) => {
    const dispatch = useAppDispatch();
    const [taskId, setTaskId] = useState<string | null>(null);
    const [generateShortStory, { isLoading: isMutationLoading, error: mutationError }] = useGenerateShortStoryMutation();

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
                    dispatch(clearProgress(taskId));
                }
                setTaskId(null);
            }, 5000);
            return () => {
                clearTimeout(timer);
            }
        }
    }, [progress?.isComplete, progress?.error, wasClearedPrematurely, taskId, dispatch]);

    const startGeneration = useCallback(async ({ topic, genre }: GenerationParams) => {
        if (isMutationLoading || taskId) return; // Prevent starting a new generation if one is active

        try {
            const { taskId: newTaskId } = await generateShortStory({ language, difficulty, topic, genre }).unwrap();
            dispatch(startGenerationTracking({ taskId: newTaskId, language, difficulty }));
            setTaskId(newTaskId);
        }
        catch (err) {
            console.error('Failed to start short story generation:', err);
            setTaskId(null);
        }
    }, [generateShortStory, language, difficulty, taskId, isMutationLoading, dispatch]);

    const isComplete = !!progress?.isComplete;
    const generationError = mutationError || progress?.error || (wasClearedPrematurely ? 'Connection to the server was lost.' : undefined);
    const isLoading = isMutationLoading || (!!taskId && !isComplete && !generationError && !wasClearedPrematurely);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? `Short story generation failed: ${generationError}` : null,
        isComplete: isComplete,
    };
};