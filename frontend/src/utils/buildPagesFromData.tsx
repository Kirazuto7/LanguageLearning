import VocabularyPage from "../components/lessons/VocabularyPage";
import BookPage from "../components/studybook/BookPage";
import ChapterPage from "../components/studybook/ChapterPage";
import { PageDTO, LessonBookDTO, ChapterDTO } from "../types/dto";

// Determines Page Component layout depending on the lesson type of the page
const getPageFromLessonType = (page: PageDTO): React.ReactElement => {
    if (!page || !page.lesson) {
        return <p>Invalid page data.</p>;
    }

    switch (page.lesson.type) {
        case 'VOCABULARY':
            return <VocabularyPage lesson={page.lesson}/>;
        default:
            return <div style={{height: '100%'}}><p>Unsupported lesson type: {page.lesson.type}</p></div>;
    }
}

const buildPagesForChapter = (chapter: ChapterDTO): React.ReactElement[] => {
    if(!chapter.pages || chapter.pages.length === 0) {
        return [];
    }

    const pages: React.ReactElement[] = [];

    const firstPage = chapter.pages[0];
    pages.push(
        <ChapterPage
            key={`chapter-title-${chapter.id}`} 
            pageNumber={ firstPage.pageNumber } 
            isRightPage={ firstPage.pageNumber % 2 === 0 } 
            chapterNumber={chapter.chapterNumber} 
            chapterNativeTitle={chapter.nativeTitle}
            chapterTitle={chapter.title}
        >
            {getPageFromLessonType(firstPage)}
        </ChapterPage>
    )

    // Loop through the page(s) data for the chapter starting from the 2nd page
    chapter.pages.slice(1).forEach(page => {
        pages.push(
            <BookPage
                key={`page-${page.id}`}
                pageNumber={page.pageNumber}
                isRightPage={page.pageNumber % 2 === 0}
            >
                {getPageFromLessonType(page)}
            </BookPage>
        )
    });
    return pages;
}

// Helper function to process the Book Data into Component Pages
export function buildPagesFromBookData(bookData: LessonBookDTO | null): React.ReactElement[] {
    if(!bookData || !bookData.chapters) return [];
    // Loop through the book chapters and build each page
    return bookData.chapters.flatMap(buildPagesForChapter);
};
