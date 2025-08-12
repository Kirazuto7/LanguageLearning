import React, { createContext, useContext, useMemo } from 'react';
import { useBookManager } from '../hooks/useBookManager';
import { useLanguage } from './LanguageSettingsContext';
import { ChapterDTO } from '../types/dto';


interface BookContextType {
    pages: React.ReactElement[];
    title: string;
    chapters: ChapterDTO[];
    generateChapter: (topic: string) => Promise<void>;
    generatedChapterPageNumber: number | null;
    isLoading: boolean;
    error: string | null;
}

const BookContext = createContext<BookContextType | undefined>(undefined);

export const useBook = (): BookContextType => {
    const context = useContext(BookContext);
    if(context === undefined) {
        throw new Error('useBook must be used within a BookProvider');
    }
    return context;
};

interface BookProviderProps {
    children: React.ReactNode;
}

/**
 * Provides the book's state (pages, title) and the function to process new chapters.
 */
export const BookProvider:React.FC<BookProviderProps> = ({ children }) => {
    const { language, difficulty } = useLanguage();
    const { pages, title, chapters, generateChapter, generatedChapterPageNumber, isLoading, error } = useBookManager(language, difficulty);

    // Memoize the context value to prevent unnecessary re-renders of consumers.
    // The value object will only be recreated if one of its dependencies changes.

    const value = useMemo(() => ({
        pages,
        title,
        chapters, 
        generateChapter,
        generatedChapterPageNumber,
        isLoading,
        error
    }), [pages, title, chapters, generateChapter, generatedChapterPageNumber, isLoading, error]);

    return (
        <BookContext.Provider value={value}>
            {children}
        </BookContext.Provider>
    );
};
