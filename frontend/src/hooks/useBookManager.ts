import { useState, useEffect, useMemo, useCallback } from 'react';
import { buildPagesFromBookData, buildPagesFromChapters } from '../utils/buildPagesFromData';
import { LessonBookDTO, ChapterDTO } from '../types/dto';
import React from 'react';

interface BookManagerResult {
    pages: React.ReactNode[];
    title: string;
    chapters: ChapterDTO[];
    processChapter: (chapterData: ChapterDTO) => void;
    generatedChapterPageNumber: number | null;
}

/* ----------------------------------------------------------- */
/* --- Hook to Handle Initial Fetch && Process New Chapters--- */
/* ----------------------------------------------------------- */
export function useBookManager(language: string, difficulty: string): BookManagerResult {
    const [bookData, setBookData] = useState<LessonBookDTO | null>(null); // Book Data in JSON format
    const [newChapters, setNewChapters] = useState<ChapterDTO[]>([]); // New Chapters created
    const [title, setTitle] = useState<string>(''); // Title of the Book
    const [generatedChapterPageNumber, setGeneratedChapterPageNumber] = useState<number | null>(null);

    // Makes Initial fetch for Book Data
    useEffect(() => {
        async function fetchData() {
        const response = await fetch('/api/book/fetch', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ language, difficulty }),
        });
        if (!response.ok) {
            console.log('Failed to fetch book data.');
            return;
        }
        const data: LessonBookDTO = await response.json();
        setBookData(data);
        setTitle(data.bookTitle || '');
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

    // Callback to retrieve new chapter data
    const processChapter = useCallback((chapterData: ChapterDTO) => {
        if(!chapterData) return;
        setNewChapters(prev => [...prev, chapterData]);
        const startingPage = chapterData.pages.length > 0 ? chapterData.pages[0].pageNumber : null;
        setGeneratedChapterPageNumber(startingPage);
    }, []);

    return { pages, title, chapters, processChapter, generatedChapterPageNumber };
}
