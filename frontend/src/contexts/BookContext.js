import React, { createContext, useContext, useMemo } from 'react';
import PropTypes from 'prop-types';
import { useBookManager } from '../hooks/useBookManager';
import { useLanguage } from './LanguageSettingsContext';

const BookContext = createContext();

export const useBook = () => {
    return useContext(BookContext);
};

/**
 * Provides the book's state (pages, title) and the function to process new chapters.
 * This provider now gets its value from a higher-level component,
 * which uses the useBookManager hook to manage the state.
 */
export const BookProvider = ({ children }) => {
    const { language, difficulty } = useLanguage();
    const { pages, title, processChapter } = useBookManager(language, difficulty);

    // Memoize the context value to prevent unnecessary re-renders of consumers.
    // The value object will only be recreated if one of its dependencies changes.

    const value = useMemo(() => ({
        pages,
        title,
        processChapter
    }), [pages, title, processChapter]);

    return (
        <BookContext.Provider value={value}>
            {children}
        </BookContext.Provider>
    );
};

BookProvider.propTypes = {
    children: PropTypes.node.isRequired,
};