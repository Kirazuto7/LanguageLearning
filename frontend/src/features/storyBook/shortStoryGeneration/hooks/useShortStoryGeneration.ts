import {useAppDispatch, useAppSelector} from "../../../../app/hooks";
import {useCallback, useEffect, useRef} from "react";
import {useGenerateShortStoryMutation} from "../../../../shared/api/shortStoryApiSlice";
import {
    clearProgressTask,
    selectProgressByTaskId, selectTaskIdForContext,
    startGenerationTracking
} from "../../../../widgets/progressBar/progressSlice";
import {AlertLevel, GenerationType} from "../../../../shared/types/types";
import {selectCurrentUser} from "../../../authentication/authSlice";
import {useAlert} from "../../../../shared/contexts/AlertContext";
import {addTaskToStorage} from "../../../../app/services/pollingManager";

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
    const { showAlert } = useAlert();
    const lastShownMessage = useRef<string | null>(null);

    // Get the active task ID for the current context directly from the Redux state.
    const activeTaskId = useAppSelector(state => selectTaskIdForContext(state, { language, difficulty, generationType: GenerationType.STORY }));
    const progress = useAppSelector(state => activeTaskId ? selectProgressByTaskId(state, activeTaskId) : undefined);

    useEffect(() => {
        if (progress?.message && progress.message !== lastShownMessage.current) {
            lastShownMessage.current = progress.message;

            if (progress.isError) {
                showAlert(progress.message || 'An unknown error occurred during generation.', AlertLevel.ERROR, 5000);
            }
            else {
                showAlert(progress.message, AlertLevel.INFO, 3000);
            }
        }
    }, [progress?.message, progress?.isError, showAlert]);

    const startGeneration = useCallback(async ({ topic, genre }: GenerationParams) => {
        // Prevent starting a new generation if one is already active for this context.
        if (isMutationLoading || activeTaskId || !user) return;

        try {
            const { taskId: newTaskId, shortStory } = await generateShortStory({ language, difficulty, topic, genre }).unwrap();
            addTaskToStorage(newTaskId);
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
    const generationError = mutationError || (progress?.isError ? progress.message : null);
    const isLoading = isMutationLoading || (!!activeTaskId && !isComplete && !generationError);

    // When a task completes or errors, this effect will trigger the cleanup after a delay.
    useEffect(() => {
        if (activeTaskId && progress && (progress.isComplete || progress.isError)){
            const timer = setTimeout(() => {
                dispatch(clearProgressTask(activeTaskId));
                lastShownMessage.current = null;
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [activeTaskId, progress, dispatch]);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? `Short story generation failed: ${generationError}` : null,
        isComplete: isComplete,
    };
};