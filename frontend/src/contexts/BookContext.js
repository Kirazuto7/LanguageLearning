import React, { createContext, useState, useContext, useCallback } from 'react';
import BookPage from '../components/BookPage';

const BookContext = createContext();

export const useBook = () => {
    return useContext(BookContext);
};

const InitialLeftPage = () => (
    <>
        <h1>Welcome to your Language Learning Book</h1>
        <p>This is an interactive book where you can generate lessons on any topic.</p>
        <h2>How to start:</h2>
        <ul>
            <li>Use the settings gear to pick your language and level.</li>
            <li>Suggest a topic in the input box below.</li>
            <li>Press "Send" to generate your custom lesson!</li>
        </ul>
        <p>Your new lesson(s) will appear here.</p>
    </>
);

const InitialRightPage = () => (
    <div>
        <h2>Ready to Begin?</h2>
        <p>
            Every journey starts with a single step. Here are some tips to make the most of your lessons:
        </p>
        <ul>
            <li>Set a regular study schedule—even 10 minutes a day helps!</li>
            <li>Don’t be afraid to make mistakes. Practice makes progress.</li>
            <li>Try writing your own sentences using new words you learn.</li>
            <li>Speak out loud to improve your pronunciation and confidence.</li>
        </ul>
        <p>
            Let’s make language learning fun and effective together!
        </p>
        <h2>What Will You Learn Today?</h2>
        <p>
            Not sure where to start? Try one of these topics:
        </p>
        <ul>
            <li>Ordering food at a restaurant</li>
            <li>Introducing yourself</li>
            <li>Travel and directions</li>
            <li>Shopping and prices</li>
            <li>Talking about hobbies</li>
        </ul>
        <p>
            Type your own topic below and let’s get started!
        </p>
    </div>
);

export const BookProvider = ({ children }) => {
    const [pages, setPages] = useState([
        <BookPage key="initial-1" pageNumber={1} isRightPage={false}><InitialLeftPage /></BookPage>,
        <BookPage key="initial-2" pageNumber={2} isRightPage={true}><InitialRightPage /></BookPage>
    ]);
    const [title, setTitle] = useState('');

    /**
     * Processes raw chapter data from the API into pages for the FlipBook.
     * This function can be expanded to handle more complex data structures.
     * @param {object} chapterData - The raw data object from the generation API.
     */
    const processChapter = useCallback((chapterData) => {
        if (!chapterData || !chapterData.lessons || chapterData.lessons.length === 0) {
            setTitle('');
            // Reset to initial page if data is invalid or cleared
            setPages([
                <BookPage key="initial-1" pageNumber={1} isRightPage={false}><InitialLeftPage /></BookPage>,
                <BookPage key="initial-2" pageNumber={2} isRightPage={true}><InitialRightPage /></BookPage>
            ]);
            return;
        }

        const newPages = chapterData.lessons.map((lesson, index) => (
            <BookPage key={`lesson-${index}`} pageNumber={index + 1} isRightPage={(index + 1) % 2 !== 0}>
                {/* The lesson data is an object. Displaying as raw JSON for now. */}
                <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word', fontSize: '12px' }}>
                    {JSON.stringify(lesson, null, 2)}
                </pre>
            </BookPage>
        ));

        setTitle(chapterData.title || 'Generated Book');
        setPages(newPages);
    }, []);

    const value = { pages, title, processChapter };

    return (
        <BookContext.Provider value={value}>
            {children}
        </BookContext.Provider>
    );
};