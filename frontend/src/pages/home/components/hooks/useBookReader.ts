import { useEffect } from 'react';
import { useLazyGetLessonBookByIdQuery } from '../../../../shared/api/lessonBookApiSlice';
import { useLazyGetStoryBookByIdQuery } from '../../../../shared/api/storyBookApiSlice';
import { BookType } from '../../../../shared/types/types';

interface UseBookReaderProps {
    bookId: number;
    bookType: BookType;
    show: boolean;
}

export const useBookReader = ({ bookId, bookType, show }: UseBookReaderProps) => {
    const [triggerGetLessonBook, { data: lessonBook, isFetching: lessonBookIsFetching, isError: lessonBookIsError }] = useLazyGetLessonBookByIdQuery();
    const [triggerGetStoryBook, { data: storyBook, isFetching: storyBookIsFetching, isError: storyBookIsError }] = useLazyGetStoryBookByIdQuery();

    useEffect(() => {
        if (!show || !bookId) return;

        switch (bookType) {
            case BookType.LESSON:
                triggerGetLessonBook(bookId);
                break;
            case BookType.STORY:
                triggerGetStoryBook(bookId);
                break;
        }
    }, [show, bookId, bookType, triggerGetLessonBook, triggerGetStoryBook]);

    const isLoading = bookType === BookType.LESSON ? lessonBookIsFetching : storyBookIsFetching;
    const isError = bookType === BookType.LESSON ? lessonBookIsError : storyBookIsError;
    const title = bookType === BookType.LESSON ? lessonBook?.title : storyBook?.title;

    return {
        isLoading,
        isError,
        lessonBook,
        storyBook,
        title
    };
};