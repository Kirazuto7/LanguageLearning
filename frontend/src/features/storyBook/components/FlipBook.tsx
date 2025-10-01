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
    stories: ShortStoryDTO[];
    title: string;
}

const FlipBook: React.FC<FlipBookProps> = ({ stories, title }) => {
    const flipBookRef = useRef<PageFlipAPI | null>(null);

    const pages = useMemo(() => buildPagesFromStoryData(stories), [stories])

    const handleTocNavigate = (pageIndex: number) => {
        if(flipBookRef.current) {
            // Offset for static pages: Cover(1) + BehindCover (1) + TOC (1) = 3
            // The pageIndex from TOC is 1-based so we substract 1 for the 0-based index in the pages array
            flipBookRef.current.pageFlip().flip(pageIndex - 1 + 3);
        }
    }

    const tocEntries: TocEntry[] = useMemo(() => {
        let runningPageIndex = 1; // Start with 1 for 1-based indexing
        return stories.map((shortStory: ShortStoryDTO, storyIndex: number) => {
            const navigationPageIndex = runningPageIndex;

            // Update the running total for the next story
            runningPageIndex += shortStory.storyPages.length;

            return {
                entryNumber: storyIndex + 1,
                title: shortStory.title,
                navigationPageIndex: navigationPageIndex,
            };
        });
    }, [stories]);

    /*const tocEntries: TocEntry[] = stories.map( (shortStory: ShortStoryDTO, storyIndex: number) => {
        // Calculate the starting page index for this story by summing the pages of all previous stories.
        const navigationPageIndex = stories
            .slice(0, storyIndex) // Get all stories before the current one
            .reduce((acc, s) => acc + s.storyPages.length, 1);

        return {
            entryNumber: storyIndex + 1,
            title: shortStory.title,
            navigationPageIndex: navigationPageIndex,
        }
    });*/

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