import { useState, useMemo, useEffect, useCallback } from "react";
import { useSwipeable } from "react-swipeable";
import { LessonChapterDTO } from "../../../shared/types/dto";
import { buildPagesForChapter } from "../utils/buildPagesFromLessonData";

interface UseLessonBookViewerProps {
    chapters: LessonChapterDTO[];
    activeChapterIndex: number;
    onAllCorrect?: () => void;
}

export const useLessonBookViewer = ({ chapters, activeChapterIndex, onAllCorrect }: UseLessonBookViewerProps) => {
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

    const handlePageSelect = (selectedIndex: number) => {
        setActivePageIndex(selectedIndex);
    };

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

    return {
        activePageIndex,
        chapterPages,
        handlePageSelect,
        swipeGestures
    }
};