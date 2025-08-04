import React, { createContext, useContext } from 'react';

const BookContext = createContext();

export const useBook = () => {
    return useContext(BookContext);
};

/**
 * Provides the book's state (pages, title) and the function to process new chapters.
 * This provider now gets its value from a higher-level component,
 * which uses the useBookManager hook to manage the state.
 */
export const BookProvider = ({ children, pages, title, processChapter }) => {
    return (
        <BookContext.Provider value={{ pages, title, processChapter }}>
            {children}
        </BookContext.Provider>
    );
};