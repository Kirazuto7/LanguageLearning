import React, { useState, useEffect, useCallback } from "react";
import { useDispatch } from "react-redux";
import { useGenerateChapterMutation } from "../features/api/bookApiSlice";
import {ChapterDTO, ChapterGenerationRequest} from "../types/dto";
import { apiSlice } from "../features/api/apiSlice";


interface ProgressUpdate {
    progress: number;
    message: string;
}

interface CompleteUpdate {
    progress: number;
    message: string;
    data: ChapterDTO;
}

export const useChapterGeneration = () => {
    const [progress, setProgress] = useState(0);
    const [message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [newChapter, setNewChapter] = useState<ChapterDTO | null>(null);

    const [generateChapter, { isLoading: isMutationLoading }] = useGenerateChapterMutation();
    const dispatch = useDispatch();

    const startGeneration = useCallback(async (request: ChapterGenerationRequest) => {
        setIsLoading(true);
        setError(null);
        setProgress(0);
        setMessage('Requesting new chapter...');

        try {
            const { taskId } = await generateChapter(request).unwrap();
            const eventSource = new EventSource(`http://localhost:8080/api/chapters/progress/${taskId}`);

            eventSource.addEventListener('progress-update', (event) => {
                const data: ProgressUpdate = JSON.parse(event.data);
                setProgress(data.progress);
                setMessage(data.message);
            });

            eventSource.addEventListener('complete', (event) => {
                const update: CompleteUpdate = JSON.parse(event.data);
                setProgress(100);
                setMessage('Chapter generation complete!');
                setNewChapter(update.data);
                setIsLoading(false);
                eventSource.close();

                // Manually invalidate to trigger refetch
                dispatch(
                    apiSlice.util.invalidateTags([
                        { type: 'Book', id: `${request.language}-${request.difficulty}-${request.userId}` }
                    ])
                );
            });

            eventSource.onerror = (err) => {
                console.error('EventSource failed:', err);
                setError('A connection error occurred during chapter generation.');
                setIsLoading(false);
                eventSource.close();
            };
        }
        catch (err) {
            console.error('Failed to start chapter generation:', err);
            setError('Failed to initiate chapter generation.');
            setIsLoading(false);
        }

    }, [generateChapter, dispatch]);

    return { startGeneration, isLoading: isLoading || isMutationLoading, progress, message, error, newChapter };
};