import React, { useState, useEffect, useMemo, memo } from "react";
import styles from './lessonBookViewer.module.scss';
import { Carousel } from "react-bootstrap";
import { buildPagesForChapter } from "../utils/buildPagesFromLessonData";
import { LessonChapterDTO } from "../../../shared/types/dto";
import {useSwipeable} from "react-swipeable";
import { ChapterSelector } from "./ChapterSelector";
import { LessonPaginator } from "./LessonPaginator";

interface LessonBookViewerProps {
    title: string;
    chapters: LessonChapterDTO[];
    activeChapterIndex: number;
    setActiveChapterIndex: (index: number) => void;
    onAllCorrect?: () => void;
};

const LessonBookViewer: React.FC<LessonBookViewerProps> = ({ title, chapters, activeChapterIndex, setActiveChapterIndex, onAllCorrect }) => {
    const [activePageIndex, setActivePageIndex] = useState(0);

    const chapterPageOffsets = useMemo(() => {
        const offsets: number[] = [];
        let runningOffset = 0;
        for (const chapter of chapters) {
            offsets.push(runningOffset);
            runningOffset += chapter.lessonPages?.length || 0;
        }
        return offsets;
    }, [chapters]);

    const currentChapter = useMemo(()=> {
        return chapters?.[activeChapterIndex]
    }, [chapters, activeChapterIndex]);

    const chapterPages = useMemo(() => {
        if(!currentChapter) return [];
        const pageOffset = chapterPageOffsets[activeChapterIndex] ?? 0;
        return buildPagesForChapter(currentChapter, activeChapterIndex, pageOffset, onAllCorrect);
    }, [currentChapter, activeChapterIndex, chapterPageOffsets, onAllCorrect]);

    useEffect(() => {
        setActivePageIndex(0);
    }, [activeChapterIndex]);

    // Keyboard Navigation
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === 'ArrowRight' && activePageIndex < chapterPages.length - 1) {
                handlePageSelect(activePageIndex + 1);
            }
            else if (e.key === 'ArrowLeft' && activePageIndex > 0) {
                handlePageSelect(activePageIndex - 1);
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };

    }, [activePageIndex, chapterPages.length]);

    // Mobile Swiping Gestures
    const swipeGestures = useSwipeable({
        onSwipedLeft: () => activePageIndex < chapterPages.length - 1 && handlePageSelect(activePageIndex + 1),
        onSwipedRight: () => activePageIndex > 0 && handlePageSelect(activePageIndex - 1),
    });

    const handlePageSelect = (selectedIndex: number) => {
        setActivePageIndex(selectedIndex);
    };

    if (!chapterPages || chapterPages.length === 0) {
        return null;
    }

    return(
        <div className={"p-3 mb-5"}>

            <h2 className={`text-center mb-3 ${styles.title}`}>{title}</h2>
            <ChapterSelector
                chapters={chapters}
                activeChapterIndex={activeChapterIndex}
                onChapterSelect={setActiveChapterIndex}
            />
            <div className={`${styles['content-area']} mt-4 justify-content-center`}>
                <LessonPaginator
                    pageCount={chapterPages.length}
                    activePageIndex={activePageIndex}
                    onPageSelect={handlePageSelect}
                />
                <div {...swipeGestures} className={styles['carousel-wrapper']}>
                    <Carousel
                        interval={null}
                        indicators={false}
                        controls={false}
                        activeIndex={activePageIndex}
                        onSelect={handlePageSelect}
                        className={styles['studybook-carousel']}>
                        {chapterPages.map((lessonPage, index) =>
                            <Carousel.Item key={index} className="h-100" style={{ overflowY: 'auto' }}>
                                {lessonPage}
                            </Carousel.Item>
                        )}
                    </Carousel>
                </div>
            </div>
        </div>
    );
}

export default memo(LessonBookViewer);