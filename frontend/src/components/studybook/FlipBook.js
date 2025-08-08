import React, {useRef, useEffect} from 'react';
import styles from '../../styles/flipbook.module.css';
import HTMLFlipBook from 'react-pageflip';
import { useLanguage } from '../../contexts/LanguageContext';
import { useBook } from '../../contexts/BookContext';

function FlipBook() {
    const { difficulty, languageName } = useLanguage();
    const { pages, startPageNumberRef } = useBook();
    const flipBook = useRef(null);
    
    const onInit = () => {
        console.log("Init");
        flipBook.current.pageFlip().flip(startPageNumberRef.current);
    }

    const onUpdate = () => {
        console.log("Update");
    }

    return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>

            <HTMLFlipBook 
                key={pages.length + JSON.stringify(pages.map(p => p.key || ''))}
                ref={flipBook}
                width={450} height={600}
                showCover={true}
                drawShadow={true}
                onInit={onInit}
                onUpdate={onUpdate}>

                {/* Cover Page */}
                <div className={`${styles.cover}`}>
                    <h2 className="mt-5 text-center">{difficulty}</h2>
                    <h2 className="mt-5 text-center">{languageName}</h2>
                </div>
                
                {/* Book Pages */}
                {pages.map((page) => page)}
                
                {/* Back Cover Page */}
                <div className={styles.cover}>
                    <h2 className="mt-5 text-center">The End</h2>
                </div>
            </HTMLFlipBook> 
        </div>
    );
}

export default FlipBook;
