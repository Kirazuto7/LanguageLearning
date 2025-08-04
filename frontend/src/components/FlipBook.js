import React, {useState, useEffect, useRef} from 'react';
import styles from '../styles/flipbook.module.css';
import HTMLFlipBook from 'react-pageflip';
import { useLanguage } from '../contexts/LanguageContext';
import { useBook } from '../contexts/BookContext';

function FlipBook() {
    const { level, languageName } = useLanguage();
    const { pages } = useBook();

    return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>

            <HTMLFlipBook 
                key={pages.length + JSON.stringify(pages.map(p => p.key || ''))}
                className={styles['main-pages']}
                width={450} height={600}
                showCover={true}>

                {/* Cover Page */}
                <div className={`${styles.cover}`}>
                    <h2 className="mt-5 text-center">{level}</h2>
                    <h2 className="mt-5 text-center">{languageName}</h2>
                </div>
                
                {/* Book Pages */}
                {pages.map((page) =>
                   page
                )}
                
                {/* Back Cover Page */}
                <div className={styles.cover}>
                    <h2 className="mt-5 text-center">The End</h2>
                </div>
            </HTMLFlipBook> 
        </div>
    );
}

export default FlipBook;
