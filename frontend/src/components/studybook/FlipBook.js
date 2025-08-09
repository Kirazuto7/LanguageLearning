import React, {useRef, useEffect} from 'react';
import styles from '../../styles/flipbook.module.css';
import HTMLFlipBook from 'react-pageflip';
import { useLanguage } from '../../contexts/LanguageSettingsContext';
import { useBook } from '../../contexts/BookContext';
import BehindCoverPage from './BehindCoverPage';
import InstructionsPage from './InstructionsPage';
import TableOfContentsPage from './TableOfContentsPage';

function FlipBook() {
    const { difficulty, languageName } = useLanguage();
    const { pages, chapters } = useBook();
    const flipBook = useRef(null);
    
    const onInit = () => {
        console.log("Init");
    }

    const onUpdate = () => {
        console.log("Update");
    }

    const navigateToPage = (pageNumber) => {
        if(flipBook.current) {
            flipBook.current.pageFlip().flip(pageNumber); // Excluding the first 2 pages
        }
    }

    const tocPage = (
        <TableOfContentsPage
            key="toc"
            chapters={chapters}
            onNavigate={navigateToPage}
        />
    );

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
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>{difficulty}</h2>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>{languageName}</h2>
                </div>

                <BehindCoverPage/>
                <InstructionsPage>{tocPage}</InstructionsPage>
                
                {/* Book Pages */}
                {pages && pages.map((page) => page)}
                
                {/* Back Cover Page */}
                <div className={styles.backcover}>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>The End</h2>
                </div>
            </HTMLFlipBook> 
        </div>
    );
}

export default FlipBook;
