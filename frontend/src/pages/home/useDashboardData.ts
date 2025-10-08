import {useGetUserDashboardDataQuery} from "../../shared/api/userApiSlice";
import {useMemo} from "react";

export const useDashboardData = () => {
    const {
        data,
        isLoading,
        isError,
        error
    } = useGetUserDashboardDataQuery();

    const lessonBooks = useMemo(() => data?.lessonBooks ?? [], [data]);
    const storyBooks = useMemo(() => data?.storyBooks ?? [], [data]);
    const hasData = useMemo(() => {
        const hasLessonBooksWithContent = lessonBooks.some(book => book.pageCount > 0);
        const hasStoryBooksWithContent = storyBooks.some(book => book.pageCount > 0);
        return hasLessonBooksWithContent || hasStoryBooksWithContent;
    }, [lessonBooks, storyBooks])

    return { isLoading, isError, error, lessonBooks, storyBooks, hasData };
};