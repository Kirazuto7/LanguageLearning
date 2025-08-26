import { useMemo } from 'react';
import { ChapterDTO } from '../types/dto';
import { useSelector } from 'react-redux';
import { useFetchBookQuery } from '../features/api/bookApiSlice';
import { RootState } from '../app/store';


interface StudyBookManagerResult {
    title: string;
    chapters: ChapterDTO[];
    isLoading: boolean;
    error: string | null;
}

export function useStudyBookManager(): StudyBookManagerResult {
    // 1. Fetch user from global Redux state
    const { user } = useSelector((state: RootState) => state.auth);
    const { settings } = useSelector((state: RootState) => state.settings);
    const language = settings?.language || '';
    const difficulty = settings?.difficulty || '';
    
    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useFetchBookQuery(
        { language, difficulty, userId: user?.id as number },
        { skip: !user || !settings});


    const title = bookData?.bookTitle || 'Book Title';
    const chapters = bookData?.chapters || [];
    const error = useMemo(() => {
        if (fetchBookError) return 'Failed to fetch the book.';
        return null;
    }, [fetchBookError]);

    return { title, chapters, isLoading: isFetchingBook, error };
}
