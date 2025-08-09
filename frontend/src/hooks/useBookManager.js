import { useState, useEffect, useMemo, useCallback } from 'react';
import { buildPagesFromBookData, buildPagesFromChapters } from '../utils/buildPagesFromData';

/* ----------------------------------------------------------- */
/* --- Hook to Handle Initial Fetch && Process New Chapters--- */
/* ----------------------------------------------------------- */
export function useBookManager(language, difficulty) {
    const [bookData, setBookData] = useState(null); // Book Data in JSON format
    const [newChapters, setNewChapters] = useState([]); // New Chapters created
    const [title, setTitle] = useState(''); // Title of the Book

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
        const data = await response.json();
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
    const chapterInfo = useMemo(() => {
        if(!bookData) return [];
        return bookData.chapters.map(chapter => ({
            chapterNumber: chapter.chapterNumber,
            title: chapter.title,
            startingPageNumber: chapter.pages.length > 0 ? chapter.pages[0].pageNumber : null
        }));
    }, [bookData]);

    // Callback to retrieve new chapter data
    const processChapter = useCallback((chapterData) => {
        if(!chapterData) return;
        setNewChapters(prev => [...prev, chapterData]);
    }, []);

    return { pages: pages || [], title: title || '', chapters: chapterInfo || [], processChapter: processChapter || (() => {}) };
}
