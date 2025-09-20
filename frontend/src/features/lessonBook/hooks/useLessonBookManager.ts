import { LessonChapterDTO } from '../../../shared/types/dto';
import { useAppSelector } from "../../../app/hooks";
import { useGetLessonBookQuery } from "../../../shared/api/lessonBookApiSlice";
import { RootState } from '../../../app/store';


interface LessonBookManagerResult {
    title: string;
    chapters: LessonChapterDTO[];
    isLoading: boolean;
    error: string | null;
    language: string;
    difficulty: string;
}

export function useLessonBookManager(): LessonBookManagerResult {
    // 1. Fetch user from global Redux state
    const { user } = useAppSelector((state: RootState) => state.auth);
    const { settings } = useAppSelector((state: RootState) => state.settings);
    const language = settings?.language || '';
    const difficulty = settings?.difficulty || '';
    
    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useGetLessonBookQuery(
        { language, difficulty },
        { skip: !user || !settings || !language || !difficulty });

    return {
        title: bookData?.title || 'Book Title',
        chapters: bookData?.lessonChapters || [],
        isLoading: isFetchingBook,
        error: fetchBookError ? 'Failed to fetch the book.' : null,
        language,
        difficulty
    };
}
