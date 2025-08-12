import { useState, useEffect, useMemo, useCallback } from 'react';
import { buildPagesFromBookData, buildPagesFromChapters } from '../utils/buildPagesFromData';
import { LessonBookDTO, ChapterDTO } from '../types/dto';
import React from 'react';

interface BookManagerResult {
    pages: React.ReactElement[];
    title: string;
    chapters: ChapterDTO[];
    generateChapter: (topic: string) => Promise<void>;
    generatedChapterPageNumber: number | null;
    isLoading: boolean;
    error: string | null;
}

/* ----------------------------------------------------------- */
/* --- Hook to Handle Initial Fetch && Process New Chapters--- */
/* ----------------------------------------------------------- */
export function useBookManager(language: string, difficulty: string): BookManagerResult {
    const [bookData, setBookData] = useState<LessonBookDTO | null>(null); // Book Data in JSON format
    const [newChapters, setNewChapters] = useState<ChapterDTO[]>([]); // New Chapters created
    const [title, setTitle] = useState<string>(''); // Title of the Book
    const [generatedChapterPageNumber, setGeneratedChapterPageNumber] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    // Makes Initial fetch for Book Data
    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/api/book/fetch', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ language, difficulty }),
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch initial book data.');
                    return;
                }

                const data: LessonBookDTO = await response.json();
                setBookData(data);
                setTitle(data.bookTitle || '');
            } catch (err) {
                    setError(err instanceof Error ? err.message :'An error occurred while fetching the book.');
                    console.error(err);
            }
        }

        fetchData();
    }, [language, difficulty])

    // Array of Flipbook Component Pages using data fetched initially from the database
    const initialPages = useMemo(() => {
        if(!bookData) return [];
        return buildPagesFromBookData(bookData);
    }, [bookData]);

    // Array of Flipbook Component Pages using data fetched from new chapter requests
    const newPages = useMemo(() => {
        if(newChapters.length === 0) return [];
        return buildPagesFromChapters(newChapters);
    }, [newChapters]);

    // Combined pages
    const pages = useMemo(() => [...initialPages, ...newPages], [initialPages, newPages]);

    // Get Chapter information for the table of contents
    const chapters = useMemo((): ChapterDTO[] => [
            ...(bookData?.chapters || []),
            ...newChapters
    ], [bookData, newChapters]);

    // Internal helper function to process new chapters
    const processChapter = useCallback((chapterData: ChapterDTO) => {
        if(!chapterData) return;
        setNewChapters(prev => [...prev, chapterData]);
        const startingPage = chapterData.pages.length > 0 ? chapterData.pages[0].pageNumber : null;
        setGeneratedChapterPageNumber(startingPage);
    }, []);

    const generateChapter = useCallback(async (topic: string) => {
        setIsLoading(true);
        setError(null);

        try {
            const response = await fetch('/api/chapters/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ language, difficulty, topic }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to generate chapter. The server responded with an error.');
            }

            const data: ChapterDTO = await response.json();
            processChapter(data);

        } catch (err) {
            if(err instanceof Error)
                setError(err.message);
            else
                setError('An unknown error occurred.');
        } finally {
            setIsLoading(false);
        }
    }, [language, difficulty, processChapter]);

    return { pages, title, chapters, generateChapter, generatedChapterPageNumber, isLoading, error };
}
