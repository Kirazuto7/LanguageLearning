import {useCallback, useEffect} from "react";
import {useGenerateChapterMutation} from "../../../../shared/api/chapterApiSlice";
import {useAppDispatch, useAppSelector} from "../../../../app/hooks";
import {
    clearProgressTask,
    selectActiveTaskIdForContext,
    selectProgressByTaskId,
    startGenerationTracking
} from "../../../../widgets/progressBar/progressSlice";
import {GenerationType} from "../../../../shared/types/types";
import {selectCurrentUser} from "../../../authentication/authSlice";

/**
 * A custom hook to manage the chapter generation workflow.
 * It reads real-time progress from the global state (managed by ProgressSubscriptionManager)
 * and provides a function to start the generation.
 *
 * @param language The language of the book being updated.
 * @param difficulty The difficulty of the book being updated.
 */
export const useChapterGeneration = (language: string, difficulty: string) => {
    const dispatch = useAppDispatch();
    const [generateChapter, { isLoading: isMutationLoading, error: mutationError }] = useGenerateChapterMutation();
    const user = useAppSelector(selectCurrentUser);

    const activeTaskId = useAppSelector(state => selectActiveTaskIdForContext(state, { language, difficulty, generationType: GenerationType.CHAPTER }));

    const progress = useAppSelector(state => activeTaskId ? selectProgressByTaskId(state, activeTaskId) : undefined);

    // When a task completes or errors, this effect will trigger the cleanup after a delay.
    useEffect(() => {
        if (activeTaskId && progress && (progress.isComplete || progress.error)) {
            const timer = setTimeout(() => {
                dispatch(clearProgressTask(activeTaskId));
            }, 5000); // Keep the final state visible for 5 seconds.
            return () => clearTimeout(timer);
        }
    }, [activeTaskId, progress, dispatch]);

    const startGeneration = useCallback(async (topic: string) => {
        // Prevent starting a new generation if one is already active for this context.
        if (isMutationLoading || activeTaskId || !user) return;

        try {
            const { taskId: newTaskId, lessonChapter } = await generateChapter({ language, difficulty, topic }).unwrap();
            dispatch(startGenerationTracking({
                taskId: newTaskId,
                language,
                difficulty,
                userId: user.id,
                generationType: GenerationType.CHAPTER,
                parentId: lessonChapter.id,
            }));
        }
        catch (err) {
            console.error('Failed to start chapter generation:', err);
        }
    }, [generateChapter, language, difficulty, activeTaskId, isMutationLoading, dispatch, user]);

    const isComplete = !!progress?.isComplete;
    const generationError = mutationError || progress?.error;
    const isLoading = isMutationLoading || (!!activeTaskId && !isComplete && !generationError);

    return {
        startGeneration,
        isLoading,
        progress: progress?.progress ?? 0,
        message: progress?.message ?? (isLoading ? 'Initiating...' : ''),
        error: generationError ? `Chapter generation failed: ${generationError}` : null,
        isComplete: isComplete,
    };
};