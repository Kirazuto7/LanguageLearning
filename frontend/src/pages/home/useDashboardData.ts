import {useGetUserDashboardDataQuery} from "../../shared/api/userApiSlice";
import {useMemo} from "react";

export const useDashboardData = () => {
    const {
        data,
        isLoading,
        isError,
        error
    } = useGetUserDashboardDataQuery();

    const lessonBooks = useMemo(() => {
        return (data?.lessonBooks || []).filter(book => book.pageCount > 0);
    }, [data]);

    const storyBooks = useMemo(() => {
        return (data?.storyBooks || []).filter(book => book.pageCount > 0);
    }, [data]);

    const hasData = useMemo(() => {
        return lessonBooks.length > 0 || storyBooks.length > 0;
    }, [lessonBooks, storyBooks])

    return { isLoading, isError, error, lessonBooks, storyBooks, hasData };
};