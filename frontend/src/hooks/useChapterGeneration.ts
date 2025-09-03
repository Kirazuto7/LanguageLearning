import { useState, useEffect, useCallback } from "react";
import { useAppDispatch } from "../app/hooks";
import { useGenerateChapterMutation, useChapterGenerationProgressQuery } from "../features/api/chapterApiSlice";
import { lessonBookApiSlice } from "../features/api/lessonBookApiSlice";

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
    const { data: progressData, error: subscriptionError } = useChapterGenerationProgressQuery(taskId!, {
        skip: !taskId,
    });

    // Listens for new pages retrieved from the subscription and patches the main book cache.
    useEffect(() => {
        if (progressData?.data && progressData.chapterId) {
            const newPage  = progressData.data;
            const chapterIdToUpdate = progressData.chapterId;

            dispatch(
                lessonBookApiSlice.util.updateQueryData(
                    'getLessonBook',
                    { language, difficulty },
                    (draft) => {
                        const chapter = draft.chapters.find(c => c.id === chapterIdToUpdate);
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

    const isLoading = isMutationLoading || (taskId != null && !progressData);

    return {
        startGeneration,
        isLoading,
        progress: progressData?.progress ?? 0,
        message: progressData?.message ?? (isLoading ? 'Initiating...' : ''),
        error: mutationError || subscriptionError || progressData?.error ? 'Chapter generation failed.' : null,
        isComplete: progressData?.progress === 100,
    };
};