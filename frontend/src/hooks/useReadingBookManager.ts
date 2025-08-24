import React, { useCallback, useMemo } from 'react';
import { buildPagesFromBookData } from '../utils/buildPagesFromData';
import { ChapterDTO } from '../types/dto';
import { useSelector } from 'react-redux';
import { useFetchBookQuery, useGenerateChapterMutation } from '../features/api/bookApiSlice';
import { RootState } from '../app/store';

/*
interface ReadingBookManagerResult {
    pages: React.ReactElement[];
    title: string;
    chapters: ChapterDTO[];
    generateChapter: (topic: string) => Promise<ChapterDTO | null>;
    isLoading: boolean;
    error: string | null;
}

export function useReadingBookManager(): ReadingBookManagerResult {
    // 1. Fetch user from global Redux state
    const { user } = useSelector((state: RootState) => state.auth);
    const { settings } = useSelector((state: RootState) => state.settings);
    const language = settings?.language || '';
    const difficulty = settings?.difficulty || '';
    
    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useFetchBookQuery(
        { language, difficulty, userId: user?.id as number },
        { skip: !user || !settings});
    
    // 3. Get the mutation function(s)
    const [generateChapterMutation, { isLoading: isGeneratingChapter, error: generateChapterError }] = useGenerateChapterMutation();

    // 4. Generate Chapter function will assemble the required data and call the mutation
    const generateChapter = useCallback(async (topic: string): Promise<ChapterDTO | null> => {
        if (!user) {
            console.error("Cannot generate chapter: User is not logged in.");
            return null;
        }
        try {
            return await generateChapterMutation({ language, difficulty, topic, userId: user.id }).unwrap();
        } catch (err) {
            console.error('Failed to generate chapter:', err);
            return null;
        }
    }, [user, language, difficulty, generateChapterMutation]);

    // 5. Process book pages based on the book data state
    const pages = useMemo(() => {
        if(!bookData) return [];
        return buildPagesFromBookData(bookData);
    }, [bookData]);

    const title = bookData?.bookTitle || 'Book Title';
    const chapters = bookData?.chapters || [];
    const isLoading = isFetchingBook || isGeneratingChapter;
    const error = useMemo(() => {
        if (fetchBookError) return 'Failed to fetch the book.';
        if (generateChapterError) return 'Failed to generate the new chapter.';
        return null;
    }, [fetchBookError, generateChapterError]);

    return { pages, title, chapters, generateChapter, isLoading, error };
}*/
