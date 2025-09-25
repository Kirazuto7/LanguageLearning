import React from 'react';
import {ShortStoryDTO, StoryBookDTO, StoryContentPageDTO, StoryPageDTO, StoryVocabularyPageDTO} from '../../../shared/types/dto';
import BookPage from '../../../shared/ui/book/BookPage';
import ChapterPage from '../../../shared/ui/book/ChapterPage';
import StoryContentPage from "../components/common/StoryContentPage";
import StoryVocabularyPage from "../components/common/StoryVocabularyPage";

const getComponentForStoryPage = (storyPage: StoryPageDTO): React.ReactElement => {
    if (!storyPage) {
        return <p>Invalid story page data.</p>
    }

    switch (storyPage.type) {
        case 'CONTENT':
            return <StoryContentPage page={storyPage as StoryContentPageDTO} />;
        case 'VOCABULARY':
            return <StoryVocabularyPage page={storyPage as StoryVocabularyPageDTO} />;
        default:
            // Handle cases where the type is unknown or not supported
            const unknownPage = storyPage as any;
            return <div><p>Unsupported story page type: {unknownPage.type || 'Unknown'}</p></div>;
    }
};

/**
 * Builds the React elements for all pages within a single story chapter.
 * The first page of a short story is a special ChapterPage, and subsequent pages are regular BookPages.
 * @param story - The story data.
 * @returns An array of React elements representing the pages of the chapter.
 */
export const buildPagesForShortStory = (story: ShortStoryDTO): React.ReactElement[] => {
    if (!story.storyPages || story.storyPages.length === 0) {
        return [];
    }

    const pages: React.ReactElement[] = [];
    const firstPage = story.storyPages[0];

    // Create the title page for the short story
    pages.push(
        <ChapterPage
            key={`story-title-${story.id}`}
            pageNumber={firstPage.pageNumber}
            isRightPage={firstPage.pageNumber % 2 === 0}
            chapterNumber={story.chapterNumber}
            chapterNativeTitle={story.nativeTitle}
            chapterTitle={story.title}
        >
            {getComponentForStoryPage(firstPage)}
        </ChapterPage>
    );

    // Create the content pages for the rest of the short story
    story.storyPages.slice(1).forEach(page => {
        pages.push(
            <BookPage key={`page-${page.id}`} pageNumber={page.pageNumber} isRightPage={page.pageNumber % 2 === 0}>
                {getComponentForStoryPage(page)}
            </BookPage>
        );
    });

    return pages;
};

export const buildPagesFromStoryData = (shortStories: ShortStoryDTO[] | null): React.ReactElement[] => {
    if (!shortStories) return [];
    return shortStories.flatMap(story => buildPagesForShortStory(story));
};