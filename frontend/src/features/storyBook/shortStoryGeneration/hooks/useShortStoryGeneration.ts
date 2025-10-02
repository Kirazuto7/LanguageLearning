import {useAppDispatch, useAppSelector} from "../../../../app/hooks";
import {useCallback, useEffect} from "react";
import {useGenerateShortStoryMutation} from "../../../../shared/api/shortStoryApiSlice";
import {
    clearProgressTask,
    selectActiveTaskIdForContext,
    selectProgressByTaskId,
    startGenerationTracking
} from "../../../../widgets/progressBar/progressSlice";
import {GenerationType} from "../../../../shared/types/types";
import {selectCurrentUser} from "../../../authentication/authSlice";

interface GenerationParams {
    topic?: string;
    genre?: string;
}

/**
 * A custom hook to manage the short story generation workflow.
 * It reads real-time progress from the global state (managed by ProgressSubscriptionManager)
 * and provides a function to start the generation.
 *
 * @param language The language of the story book being updated.
 * @param difficulty The difficulty of the story book being updated.
 */
export const useShortStoryGeneration = (language: string, difficulty: string) => {
    const dispatch = useAppDispatch();
    const [generateShortStory, { isLoading: isMutationLoading, error: mutationError }] = useGenerateShortStoryMutation();
    const user = useAppSelector(selectCurrentUser);

    // Get the active task ID for the current context directly from the Redux state.
    const activeTaskId = useAppSelector(state => selectActiveTaskIdForContext(state, { language, difficulty, generationType: GenerationType.STORY }));


    const progress = useAppSelector(state => activeTaskId ? selectProgressByTaskId(state, activeTaskId) : undefined);

    // When a task completes or errors, this effect will trigger the cleanup after a delay.
    useEffect(() => {
        if (activeTaskId && progress && (progress.isComplete || progress.error)){
            const timer = setTimeout(() => {
                dispatch(clearProgressTask(activeTaskId));
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [activeTaskId, progress, dispatch]);

    const startGeneration = useCallback(async ({ topic, genre }: GenerationParams) => {
        // Prevent starting a new generation if one is already active for this context.
        if (isMutationLoading || activeTaskId || !user) return;

        try {
            const { taskId: newTaskId, shortStory } = await generateShortStory({ language, difficulty, topic, genre }).unwrap();
            dispatch(startGenerationTracking({
                taskId: newTaskId,
                language,
                difficulty,
                userId: user.id,
                generationType: GenerationType.STORY,
                parentId: String(shortStory.id),
            }));
        }
        catch (err) {
            console.error('Failed to start short story generation:', err);
        }
    }, [generateShortStory, language, difficulty, activeTaskId, isMutationLoading, dispatch, user]);

    const isComplete = !!progress?.isComplete;
    const generationError = mutationError || progress?.error;
    const isLoading = isMutationLoading || (!!activeTaskId && !isComplete && !generationError);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? `Short story generation failed: ${generationError}` : null,
        isComplete: isComplete,
    };
};