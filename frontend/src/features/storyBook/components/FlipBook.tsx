import React, {useMemo, useRef} from 'react';
import styles from './flipbook.module.scss';
import HTMLFlipBook from 'react-pageflip';
import BehindCoverPage from './common/BehindCoverPage';
import { ShortStoryDTO } from "../../../shared/types/dto";
import {buildPagesFromStoryData} from "../utils/buildPagesFromStoryData";
import TableOfContentsPage, { TocEntry } from "../../../shared/ui/book/TableOfContentsPage";
import BookPage from "../../../shared/ui/book/BookPage";

interface PageFlipAPI {
    pageFlip: () => {
        flip: (pageIndex: number, corner?: string) => void;
    };
}

interface FlipBookProps {
    navigateToPage: number | null;
    onFlipComplete: () => void;
    stories: ShortStoryDTO[];
    title: string;
}

const FlipBook: React.FC<FlipBookProps> = ({ navigateToPage, onFlipComplete, stories, title }) => {
    const flipBookRef = useRef<PageFlipAPI | null>(null);

    const pages = useMemo(() => buildPagesFromStoryData(stories), [stories])

    const onInit = () => {
        // The number of static pages before the dynamic content (Cover, BehindCover, TOC)
        const pageIndexOffset = 2;
        if(flipBookRef.current && navigateToPage && navigateToPage > 0) {
            flipBookRef.current.pageFlip().flip(navigateToPage + pageIndexOffset);
            onFlipComplete();
        }
    }

    const handleTocNavigate = (pageIndex: number) => {
        if(flipBookRef.current) {
            // The number of static pages before the dynamic content (Cover, BehindCover)
            const pageIndexOffset = 1;
            flipBookRef.current.pageFlip().flip(pageIndex+pageIndexOffset);
        }
    }

    const tocEntries: TocEntry[] = stories.map( (shortStory: ShortStoryDTO) => {
        const firstPageOfStory = shortStory.storyPages?.[0];
        const navigationPageIndex = firstPageOfStory?.pageNumber ?? 0;
        return {
            entryNumber: shortStory.chapterNumber,
            title: shortStory.title,
            navigationPageIndex: navigationPageIndex,
        }
    });

    return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>

            <HTMLFlipBook
                key={pages.length + JSON.stringify(pages.map((p: React.ReactElement) => p.key || ''))}
                ref={(el) => (flipBookRef.current = el as PageFlipAPI)}
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
                disableFlipByClick={false}
                useMouseEvents={true}
                swipeDistance={30}
                showPageCorners={true}
                onInit={onInit}
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