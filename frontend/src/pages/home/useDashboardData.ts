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
        return lessonBooks.length > 0 || storyBooks.length > 0;
    }, [lessonBooks, storyBooks])

    return { isLoading, isError, error, lessonBooks, storyBooks, hasData };
};