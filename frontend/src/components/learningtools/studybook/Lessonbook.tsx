import React, { useState, useEffect, useMemo } from "react";
import styles from './lessonbook.module.scss';
import { Carousel, Pagination, Spinner, Form } from "react-bootstrap";
import { useStudyBookManager } from "../../../hooks/useStudyBookManager";
import { buildPagesForChapter } from "../../../utils/buildPagesFromData";
interface LessonbookProps{
};

const Lessonbook: React.FC<LessonbookProps> = ({}) => {
    const { chapters, title, isLoading } = useStudyBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState(0);
    const [activePageIndex, setActivePageIndex] = useState(0);

    const currentChapter = useMemo(()=> {
        return chapters?.[activeChapterIndex]
    }, [chapters, activeChapterIndex]);

    const chapterPages = useMemo(() => {
        if(!currentChapter) return [];
        return buildPagesForChapter(currentChapter);
    }, [currentChapter]);

    useEffect(() => {
        setActivePageIndex(0);
    }, [activeChapterIndex]);

    const handlePageSelect = (selectedIndex: number) => {
        setActivePageIndex(selectedIndex);
    };

    const handleChapterSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setActiveChapterIndex(Number(e.target.value));
    };

    if (isLoading) {
        return <div className="d-flex justify-content-center align-items-center h-100"><Spinner animation="border" /></div>;
    }

    if (!chapterPages || chapterPages.length === 0) {
        return <div className="text-center h-100 d-flex align-items-center justify-content-center">Select a topic to begin.</div>;
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
        <div className={"p-3"}>

            <h2 className={`text-center mb-3 ${styles.title}`}>{title}</h2>
            {renderChapterSelector()}
            <div className={`${styles['content-area']} mt-4`}>
                <Pagination className={`${styles['page-control-container']} flex-column align-items-center align-self-center`}>
                    <Pagination.Prev className={`${styles['page-control-item']} mb-4`} onClick={() => handlePageSelect(activePageIndex - 1)} disabled={activePageIndex === 0}/>
                    {paginationItems}
                    <Pagination.Next className={`${styles['page-control-item']} mt-4`} onClick={() => handlePageSelect(activePageIndex + 1)} disabled={activePageIndex === chapterPages.length-1}/>
                </Pagination>
                <Carousel
                    interval={null}
                    indicators={false}
                    controls={false}
                    activeIndex={activePageIndex}
                    onSelect={handlePageSelect}
                    className={styles['studybook-carousel']}>
                    {chapterPages.map((page: React.ReactElement, index) =>
                        <Carousel.Item key={index} className="h-100" style={{ overflowY: 'auto' }}>
                            {page}
                        </Carousel.Item>
                    )}
                </Carousel>
            </div>
        </div>
    );
}

export default Lessonbook;