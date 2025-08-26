import VocabularyLesson from "../components/learningtools/studybook/lessons/VocabularyLesson";
import GrammarLesson from "../components/learningtools/studybook/lessons/GrammarLesson";
import BookPage from "../components/bookpages/BookPage";
import ChapterPage from "../components/bookpages/ChapterPage";
import { PageDTO, LessonBookDTO, ChapterDTO } from "../types/dto";
import PracticeLesson from "../components/learningtools/studybook/lessons/PracticeLesson";
import ReadingComprehensionLesson from "../components/learningtools/studybook/lessons/ReadingComprehensionLesson";
import ConjugationLesson from "../components/learningtools/studybook/lessons/ConjugationLesson";

const getPageFromLessonType = (page: PageDTO, onAllCorrect?: () => void): React.ReactElement => {
    if (!page || !page.lesson) {
        return <p>Invalid page data.</p>;
    }

    switch (page.lesson.type) {
        case 'VOCABULARY':
            return <VocabularyLesson lesson={page.lesson}/>;
        case 'GRAMMAR':
            return <GrammarLesson lesson={page.lesson}/>;
        case 'CONJUGATION':
            return <ConjugationLesson lesson={page.lesson}/>;
        case 'PRACTICE':
            return <PracticeLesson lesson={page.lesson}/>;
        case 'READING_COMPREHENSION':
            return <ReadingComprehensionLesson lesson={page.lesson} onAllCorrect={onAllCorrect} />;
        default:
            const unknownLesson = page.lesson as { type?: string };
            return <div style={{height: '100%'}}><p>Unsupported lesson type: {unknownLesson.type || 'Unknown'}</p></div>;
    }
}

export const buildPagesForChapter = (chapter: ChapterDTO, onAllCorrect?: () => void): React.ReactElement[] => {
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
            {getPageFromLessonType(firstPage, onAllCorrect)}
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
                {getPageFromLessonType(page, onAllCorrect)}
            </BookPage>
        )
    });
    return pages;
}

// Helper function to process the Book Data into Component Pages
export function buildPagesFromBookData(bookData: LessonBookDTO | null): React.ReactElement[] {
    if(!bookData || !bookData.chapters) return [];
    // Loop through the book chapters and build each page
    return bookData.chapters.flatMap(chapter => buildPagesForChapter(chapter));
};
