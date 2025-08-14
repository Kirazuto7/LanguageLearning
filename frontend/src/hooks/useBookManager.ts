import React, { useMemo } from 'react';
import { buildPagesFromBookData } from '../utils/buildPagesFromData';
import { ChapterDTO } from '../types/dto';
import { useSelector } from 'react-redux';
import { useFetchBookQuery, useGenerateChapterMutation } from '../features/books/bookApiSlice';
import { RootState } from '../app/store';


interface BookManagerResult {
    pages: React.ReactElement[];
    title: string;
    chapters: ChapterDTO[];
    generateChapter: (topic: string) => Promise<ChapterDTO | null>;
    isLoading: boolean;
    error: string | null;
}

export function useBookManager(language: string, difficulty: string): BookManagerResult {
    // 1. Fetch user from global Redux state
    const { user } = useSelector((state: RootState) => state.auth);

    // 2. Fetch the book data
    const { data: bookData, isLoading: isFetchingBook, error: fetchBookError } = useFetchBookQuery({ language, difficulty });
    
    // 3. Get the mutation function(s)
    const [generateChapterMutation, { isLoading: isGeneratingChapter, error: generateChapterError }] = useGenerateChapterMutation();

    // 4. Generate Chapter function will assemble the required data and call the mutation
    const generateChapter = async (topic: string) => {
        if (!user) {
            console.error("Cannot generate chapter: User is not logged in.");
            return null;
        }
        return generateChapterMutation({ language, difficulty, topic, userId: user.id }).unwrap();
    };

    // 5. Process book pages based on the book data state
    const pages = useMemo(() => {
        if(!bookData) return [];
        return buildPagesFromBookData(bookData);
    }, [bookData]);

    const title = bookData?.bookTitle || 'Book Title';
    const chapters = bookData?.chapters || [];
    const isLoading = isFetchingBook || isGeneratingChapter;
    const error = fetchBookError || generateChapterError ? 'An error occurred.' : null;

    return { pages, title, chapters, generateChapter, isLoading, error };
}
