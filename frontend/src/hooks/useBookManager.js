import React, { useState, useRef, useEffect, useCallback } from 'react';
import BookPage from '../components/studybook/BookPage';
import VocabularyLesson from '../components/lessons/VocabularyLesson';
import InitialLeftPage from '../components/studybook/InitialLeftPage';
import InitialRightPage from '../components/studybook/InitialRightPage';

const initialPages = [
    <BookPage key="initial-1" pageNumber={1} isRightPage={false}><InitialLeftPage /></BookPage>,
    <BookPage key="initial-2" pageNumber={2} isRightPage={true}><InitialRightPage /></BookPage>
];

export const useBookManager = () => {
    const [pages, setPages] = useState(initialPages);
    const [title, setTitle] = useState('');
    //const [startPageNumber, setStartPageNumber] = useState(1);
    const startPageNumberRef = useRef(1);

    useEffect(() => {
        // Keep track of the previous page number before the effect updates for the next page
        startPageNumberRef.current = pages.length;
    }, [pages])

    const processChapter = useCallback((chapterData) => {
        console.log(chapterData);
        // If the data is empty, reuse the initial state
        if (!chapterData || !chapterData.pages || chapterData.pages.length === 0) {
            setTitle('');
            setPages(initialPages);
            return;
        }

        setTitle(chapterData.title || 'Generated Book');
        
        setPages(prevPages => {
            // If the book is in its initial state, replace it. Otherwise append
            // It correctly identifies the initial state as long as the first page has this key.
            const isInitialState = prevPages.length === 0 || (prevPages.length > 0 && prevPages[0].key === 'initial-1');
            const basePages = isInitialState ? [] : prevPages;
            const newStartPageNumber = basePages.length + 1;
            
            // Create a title page with correct new page number
            const titlePage = (
                // A right page has an even number. Page 1 should be a left page.
                <BookPage key={`page-${newStartPageNumber }`} pageNumber={newStartPageNumber } isRightPage={newStartPageNumber  % 2 === 0}>
                    <div className="text-center" style={{ paddingTop: '100px', height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}> 
                        <h1>{chapterData.title}</h1>
                        <h3 className="text-muted mt-3">{chapterData.nativeTitle}</h3>
                    </div>
                </BookPage>
            );

            // Create pages for each lesson with continuous page numbering
            const lessonPages = chapterData.lessons.map((lesson, index) => {
                const pageNumber = newStartPageNumber  + 1 + index;
                let content;

                switch (lesson.type) {
                    case 'vocabulary':
                        content = <VocabularyLesson lesson={lesson} />;
                        break;
                    default:
                        content = <p>Unsupported lesson type: {lesson.type}</p>;
                }
                return <BookPage key={`page-${pageNumber}`} pageNumber={pageNumber} isRightPage={pageNumber % 2 === 0}>{content}</BookPage>;
            });
      
            return [...basePages, titlePage, ...lessonPages];
        }); // This closes the setPages updater function.
            
    }, []); // This closes the useCallback hook.

    // Return the state and the function to update it.
    return { pages, title, startPageNumberRef,processChapter };
};