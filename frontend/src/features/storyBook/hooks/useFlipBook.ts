import { useMemo, useRef } from 'react';
import { ShortStoryDTO } from "../../../shared/types/dto";
import { buildPagesFromStoryData } from "../utils/buildPagesFromStoryData";
import { TocEntry } from "../../../shared/ui/book/TableOfContentsPage";

export interface PageFlipAPI {
    pageFlip: () => {
        flip: (pageIndex: number, corner?: string) => void;
    };
}

interface UseFlipBookProps {
    stories: ShortStoryDTO[];
}

export const useFlipBook = ({ stories }: UseFlipBookProps) => {
    const flipBookRef = useRef<PageFlipAPI | null>(null);

    const pages = useMemo(() => buildPagesFromStoryData(stories), [stories]);

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

    const handleTocNavigate = (pageIndex: number) => {
        if(flipBookRef.current) {
            // Offset for static pages: Cover(1) + BehindCover (1) + TOC (1) = 3
            // The pageIndex from TOC is 1-based so we substract 1 for the 0-based index in the pages array
            flipBookRef.current.pageFlip().flip(pageIndex - 1 + 3);
        }
    }

    return {
        flipBookRef,
        pages,
        tocEntries,
        handleTocNavigate,
    };
};