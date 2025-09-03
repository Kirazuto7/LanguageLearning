import { ChapterDTO } from '../types/dto';
import { useAppSelector } from "../app/hooks";
import { useGetLessonBookQuery } from "../features/api/lessonBookApiSlice";
import { RootState } from '../app/store';


interface StudyBookManagerResult {
    title: string;
    chapters: ChapterDTO[];
    isLoading: boolean;
    error: string | null;
    language: string;
    difficulty: string;
}

export function useStudyBookManager(): StudyBookManagerResult {
    // 1. Fetch user from global Redux state
    const { user } = useAppSelector((state: RootState) => state.auth);
    const { settings } = useAppSelector((state: RootState) => state.settings);
    const language = settings?.language || '';
    const difficulty = settings?.difficulty || '';
    
    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useGetLessonBookQuery(
        { language, difficulty },
        { skip: !user || !settings});

    return {
        title: bookData?.bookTitle || 'Book Title',
        chapters: bookData?.chapters || [],
        isLoading: isFetchingBook,
        error: fetchBookError ? 'Failed to fetch the book.' : null,
        language,
        difficulty
    };
}
