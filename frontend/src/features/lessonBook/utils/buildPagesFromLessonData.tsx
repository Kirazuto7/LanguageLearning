import VocabularyLesson from "../../lessons/components/VocabularyLesson";
import GrammarLesson from "../../lessons/components/GrammarLesson";
import BookPage from "../../../shared/ui/book/BookPage";
import ChapterPage from "../../../shared/ui/book/ChapterPage";
import { LessonPageDTO, LessonBookDTO, LessonChapterDTO } from "../../../shared/types/dto";
import PracticeLesson from "../../lessons/components/PracticeLesson";
import ReadingComprehensionLesson from "../../lessons/components/ReadingComprehensionLesson";
import ConjugationLesson from "../../lessons/components/ConjugationLesson";

const getPageFromLessonType = (lessonPage: LessonPageDTO, onAllCorrect?: () => void): React.ReactElement => {
    if (!lessonPage || !lessonPage.lesson) {
        return <p>Invalid lessonPage data.</p>;
    }

    switch (lessonPage.lesson.type) {
        case 'VOCABULARY':
            return <VocabularyLesson lesson={lessonPage.lesson}/>;
        case 'GRAMMAR':
            return <GrammarLesson lesson={lessonPage.lesson}/>;
        case 'CONJUGATION':
            return <ConjugationLesson lesson={lessonPage.lesson}/>;
        case 'PRACTICE':
            return <PracticeLesson lesson={lessonPage.lesson}/>;
        case 'READING_COMPREHENSION':
            return <ReadingComprehensionLesson lesson={lessonPage.lesson} onAllCorrect={onAllCorrect} />;
        default:
            const unknownLesson = lessonPage.lesson as { type?: string };
            return <div style={{height: '100%'}}><p>Unsupported lesson type: {unknownLesson.type || 'Unknown'}</p></div>;
    }
}

export const buildPagesForChapter = (lessonChapter: LessonChapterDTO, onAllCorrect?: () => void): React.ReactElement[] => {
    if(!lessonChapter.lessonPages || lessonChapter.lessonPages.length === 0) {
        return [];
    }

    const pages: React.ReactElement[] = [];

    const firstPage = lessonChapter.lessonPages[0];
    pages.push(
        <ChapterPage
            key={`lessonChapter-title-${lessonChapter.id}`}
            pageNumber={ firstPage.pageNumber } 
            isRightPage={ firstPage.pageNumber % 2 === 0 } 
            chapterNumber={lessonChapter.chapterNumber}
            chapterNativeTitle={lessonChapter.nativeTitle}
            chapterTitle={lessonChapter.title}
        >
            {getPageFromLessonType(firstPage, onAllCorrect)}
        </ChapterPage>
    )

    // Loop through the lessonPage(s) data for the lessonChapter starting from the 2nd lessonPage
    lessonChapter.lessonPages.slice(1).forEach(lessonPage => {
        pages.push(
            <BookPage
                key={`lessonPage-${lessonPage.id}`}
                pageNumber={lessonPage.pageNumber}
                isRightPage={lessonPage.pageNumber % 2 === 0}
            >
                {getPageFromLessonType(lessonPage, onAllCorrect)}
            </BookPage>
        )
    });
    return pages;
}

// Helper function to process the Book Data into Component Pages
export function buildPagesFromBookData(bookData: LessonBookDTO | null): React.ReactElement[] {
    if(!bookData || !bookData.lessonChapters) return [];
    // Loop through the book chapters and build each lessonPage
    return bookData.lessonChapters.flatMap(lessonChapter => buildPagesForChapter(lessonChapter));
};
