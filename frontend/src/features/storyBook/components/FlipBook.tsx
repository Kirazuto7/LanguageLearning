import React from 'react';
import styles from './flipbook.module.scss';
import HTMLFlipBook from 'react-pageflip';
import BehindCoverPage from './common/BehindCoverPage';
import { ShortStoryDTO } from "../../../shared/types/dto";
import TableOfContentsPage from "../../../shared/ui/book/TableOfContentsPage";
import BookPage from "../../../shared/ui/book/BookPage";
import { useFlipBook, PageFlipAPI } from '../hooks/useFlipBook';

interface FlipBookProps {
    stories: ShortStoryDTO[];
    title: string;
}

const FlipBook: React.FC<FlipBookProps> = ({ stories, title }) => {
    const { flipBookRef, pages, tocEntries, handleTocNavigate } = useFlipBook({ stories });

    return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>

            <HTMLFlipBook
                key={pages.length + JSON.stringify(pages.map((p: React.ReactElement) => p.key || ''))}
                ref={(el) => (flipBookRef.current = el as PageFlipAPI | null)}
                width={450} height={600}
                size="stretch"
                minWidth={315}
                maxWidth={1000}
                minHeight={420}
                maxHeight={1350}
                maxShadowOpacity={0.5}
                showCover={true}
                drawShadow={true}
                flippingTime={1000}
                usePortrait={true}
                startZIndex={0}
                autoSize={true}
                className={styles.flipBook}
                style={{}}
                startPage={0}
                mobileScrollSupport={true}
                clickEventForward={true}
                disableFlipByClick={true}
                useMouseEvents={true}
                swipeDistance={30}
                showPageCorners={true}
            >


                <div className={`${styles.cover}`}>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>{title}</h2>
                </div>

                <BehindCoverPage/>
                <BookPage pageNumber={0} isRightPage={true}>
                    <TableOfContentsPage entries={tocEntries} onNavigate={handleTocNavigate} entryPrefix="Story"/>
                </BookPage>

                {pages.map((page: React.ReactElement) => page)}
                

                <div className={styles.backcover}>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>The End</h2>
                </div>
            </HTMLFlipBook>
        </div>
    );
};

export default FlipBook;