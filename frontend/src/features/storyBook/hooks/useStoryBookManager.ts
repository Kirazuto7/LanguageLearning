import {ShortStoryDTO} from "../../../shared/types/dto";
import {useAppSelector} from "../../../app/hooks";
import {RootState} from "../../../app/store";
import {useGetStoryBookQuery} from "../../../shared/api/storyBookApiSlice";


interface StoryBookManagerResult {
    title: string;
    stories: ShortStoryDTO[];
    isLoading: boolean;
    error: string | null;
    language: string;
    difficulty: string;
}

export function useStoryBookManager(): StoryBookManagerResult {
    // 1. Fetch user and settings from global Redux state
    const { user } = useAppSelector((state: RootState) => state.auth);
    const { settings } = useAppSelector((state: RootState) => state.settings);
    const language = settings?.language || '';
    const difficulty = settings?.difficulty || '';

    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useGetStoryBookQuery(
        { language, difficulty },
        { skip: !user || !settings || !language || !difficulty });

    return {
        title: bookData?.title || 'Story Book',
        stories: bookData?.shortStories || [],
        isLoading: isFetchingBook,
        error: fetchBookError ? 'Failed to fetch the story book.' : null,
        language,
        difficulty
    };
}