import React, { useRef } from 'react';
import styles from '../../styles/flipbook.module.css';
import HTMLFlipBook from 'react-pageflip';
import { useLanguage } from '../../contexts/LanguageSettingsContext';
import BehindCoverPage from './BehindCoverPage';
import TableOfContentsPage, { TocChapter } from './TableOfContentsPage';
import BookPage from './BookPage';
import { ChapterDTO } from '../../types/dto';
import { useBookManager } from '../../hooks/useBookManager';

interface PageFlipAPI {
    flip: (pageIndex: number, corner?: string) => void;
}

interface FlipBookProps {
    generatedChapterPage: number | null;
    onFlipComplete: () => void;
}


const FlipBook: React.FC<FlipBookProps> = ({ generatedChapterPage, onFlipComplete }) => {
    const { language, difficulty, languageName } = useLanguage();
    const { pages, chapters, title } = useBookManager(language, difficulty);
    const flipBookRef = useRef<React.ElementRef<typeof HTMLFlipBook> | null>(null);

    const onInit = () => {
        if(flipBookRef.current && generatedChapterPage && generatedChapterPage > 0) {
            (flipBookRef.current.pageFlip() as PageFlipAPI).flip(generatedChapterPage+2);
            onFlipComplete();
        }
    }

    const handleChapterSelect = (pageIndex: number) => {
        if(flipBookRef.current) {
            (flipBookRef.current.pageFlip() as PageFlipAPI).flip(pageIndex);
        }
    }

    const pageIndexOffset = 2;

    const tocChapters: TocChapter[] = chapters.map( (chapter: ChapterDTO) => {
        const firstPageOfChapter = chapter.pages?.[0];
        const displayPageNumber = firstPageOfChapter?.pageNumber ?? 0;
        return {
            chapterNumber: chapter.chapterNumber,
            title: chapter.title,
            displayPageNumber: displayPageNumber,
            navigationPageIndex: displayPageNumber > 0 ? displayPageNumber + pageIndexOffset : 0
        }
    });

    return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>
            {/*@ts-ignore*/}
            <HTMLFlipBook
                key={pages.length + JSON.stringify(pages.map((p: React.ReactElement) => p.key || ''))}
                ref={flipBookRef}
                width={450} height={600}
                showCover={true}
                drawShadow={true}
                onInit={onInit}
                onUpdate={() => {}}
                onFlip={() => {}}
                onChangeState={() => {}}
                onChangeOrientation={() => {}}
            >

                {/* Cover Page */}
                <div className={`${styles.cover}`}>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>{title}</h2>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>{languageName}</h2>
                </div>

                <BehindCoverPage/>
                <BookPage pageNumber={0} isRightPage={true}>
                    <TableOfContentsPage chapters={tocChapters} onNavigate={handleChapterSelect} />
                </BookPage>
                
                {/* Book Pages */}
                {pages && pages.map((page: React.ReactElement) => page)}
                
                {/* Back Cover Page */}
                <div className={styles.backcover}>
                    <h2 className={`mt-5 text-center ${styles['book-title']}`}>The End</h2>
                </div>
            </HTMLFlipBook>
        </div>
    );
};

export default FlipBook;
