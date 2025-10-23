import React, { memo } from "react";
import styles from './lessonBookViewer.module.scss';
import { Carousel } from "react-bootstrap";
import { LessonChapterDTO } from "../../../shared/types/dto";
import { ChapterSelector } from "./ChapterSelector";
import { LessonPaginator } from "./LessonPaginator";
import {useLessonBookViewer} from "../hooks/useLessonBookViewer";
import ChapterGenerationDisplay from "./ChapterGenerationDisplay";

interface LessonBookViewerProps {
    title: string;
    chapters: LessonChapterDTO[];
    activeChapterIndex: number;
    setActiveChapterIndex: (index: number) => void;
    onAllCorrect?: () => void;
    isGenerating?: boolean;
};

const LessonBookViewer: React.FC<LessonBookViewerProps> = ({ title, chapters, activeChapterIndex, setActiveChapterIndex, onAllCorrect, isGenerating }) => {

    const { activePageIndex, chapterPages, handlePageSelect, swipeGestures } = useLessonBookViewer({ chapters, activeChapterIndex, onAllCorrect });

    if (!chapterPages || chapterPages.length === 0) {
        return isGenerating ? <ChapterGenerationDisplay/> : null;
    }

    return(
        <div className={"p-3 mb-5"}>

            <div className={`${styles.titleContainer} mt-2`}>
                <h2 className={`mb-3 ${styles.title}`}>{title}</h2>
            </div>

            <ChapterSelector
                chapters={chapters}
                activeChapterIndex={activeChapterIndex}
                onChapterSelect={setActiveChapterIndex}
            />

            <div className={`${styles['content-area']}`}>
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