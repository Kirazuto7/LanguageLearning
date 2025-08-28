import React, { useState, useEffect, useMemo, memo } from "react";
import styles from './lessonbook.module.scss';
import { Carousel, Pagination, Form } from "react-bootstrap";
import { buildPagesForChapter } from "../../../utils/buildPagesFromData";
import {ChapterDTO} from "../../../types/dto";
import {useSwipeable} from "react-swipeable";

interface LessonbookProps{
    title: string;
    chapters: ChapterDTO[];
    activeChapterIndex: number;
    setActiveChapterIndex: (index: number) => void;
    onAllCorrect?: () => void;
};

const Lessonbook: React.FC<LessonbookProps> = ({ title, chapters, activeChapterIndex, setActiveChapterIndex, onAllCorrect }) => {
    const [activePageIndex, setActivePageIndex] = useState(0);

    const currentChapter = useMemo(()=> {
        return chapters?.[activeChapterIndex]
    }, [chapters, activeChapterIndex]);

    const chapterPages = useMemo(() => {
        if(!currentChapter) return [];
        return buildPagesForChapter(currentChapter, onAllCorrect);
    }, [currentChapter, onAllCorrect]);

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

    const handleChapterSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setActiveChapterIndex(Number(e.target.value));
    };

    if (!chapterPages || chapterPages.length === 0) {
        return null;
    }

    const renderChapterSelector = () => {
        if(!chapters || chapters.length === 0) return null;
        return (<div className={`d-flex justify-content-center mb-4`}>
            <Form.Select
                aria-label={"Chapter select"}
                value={activeChapterIndex}
                onChange={handleChapterSelect}
                className={styles['chapter-select']}
            >
                {chapters.map((chapter, index)=>(
                    <option key={index} value={index}>
                        Chapter {chapter.chapterNumber}: {chapter.title}
                    </option>
                ))}
            </Form.Select>
        </div>)
    }

    const paginationItems = chapterPages.map((_, i) => {
        const isPageActive = activePageIndex === i;
        const itemClasses = [
          styles['page-item'],
          i !== chapterPages.length - 1 ? 'mb-2' : null,
          isPageActive ? styles['active-page-item'] : null,
        ].filter(Boolean).join(' ');
        return (<Pagination.Item
            className={itemClasses}
            key={i}
            active={i === activePageIndex}
            onClick={() => handlePageSelect(i)}
        >
            {i + 1}
        </Pagination.Item>
    )});

    return(
        <div className={"p-3 mb-5"}>

            <h2 className={`text-center mb-3 ${styles.title}`}>{title}</h2>
            {/** Chapter Selector **/ renderChapterSelector()}
            <div className={`${styles['content-area']} mt-4 justify-content-center`}>
                <Pagination className={`${styles['page-control-container']} flex-column align-items-center align-self-center`}>
                    <Pagination.Prev className={`${styles['page-control-item']} mb-4`} onClick={() => handlePageSelect(activePageIndex - 1)} disabled={activePageIndex === 0}/>
                    {paginationItems}
                    <Pagination.Next className={`${styles['page-control-item']} mt-4`} onClick={() => handlePageSelect(activePageIndex + 1)} disabled={activePageIndex === chapterPages.length-1}/>
                </Pagination>
                <div {...swipeGestures} className={styles['carousel-wrapper']}>
                    <Carousel
                        interval={null}
                        indicators={false}
                        controls={false}
                        activeIndex={activePageIndex}
                        onSelect={handlePageSelect}
                        className={styles['studybook-carousel']}>
                        {chapterPages.map((page, index) =>
                            <Carousel.Item key={index} className="h-100" style={{ overflowY: 'auto' }}>
                                {page}
                            </Carousel.Item>
                        )}
                    </Carousel>
                </div>
            </div>
        </div>
    );
}

export default memo(Lessonbook);