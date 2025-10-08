import {BookType} from "../../../shared/types/types";
import {useLazyGetLessonBookByIdQuery} from "../../../shared/api/lessonBookApiSlice";
import {useLazyGetStoryBookByIdQuery} from "../../../shared/api/storyBookApiSlice";
import React, {useEffect, useMemo} from "react";
import {Button, Modal, Spinner} from "react-bootstrap";
import {buildPagesFromStoryData} from "../../../features/storyBook/utils/buildPagesFromStoryData";
import styles from "./bookreadermodal.module.scss";
import HTMLFlipBook from "react-pageflip";
import {buildPagesFromBookData} from "../../../features/lessonBook/utils/buildPagesFromLessonData";

interface BookReaderModalProps {
    bookId: number;
    bookType: BookType;
    show: boolean;
    onHide: () => void;
}

const BookReaderModal: React.FC<BookReaderModalProps> = ({ bookId, bookType, show, onHide }) => {
    const [triggerGetLessonBook, { data: lessonBook, isLoading: lessonBookIsLoading, isError: lessonBookIsError }] = useLazyGetLessonBookByIdQuery();
    const [triggerGetStoryBook, { data: storyBook, isLoading: storyBookIsLoading, isError: storyBookIsError }] = useLazyGetStoryBookByIdQuery();
    const title = bookType === BookType.LESSON ? lessonBook?.title : storyBook?.title;

    useEffect(() => {
        if (show && bookType == BookType.LESSON) {
            triggerGetLessonBook(bookId);
        }
        else if (show && bookType == BookType.STORY) {
            triggerGetStoryBook(bookId);
        }
    }, [show, bookId, bookType, triggerGetLessonBook, triggerGetStoryBook]);

    const pages = useMemo(() => {
        if (bookType === BookType.STORY && storyBook) {
            return buildPagesFromStoryData(storyBook.shortStories);
        }

        if (bookType === BookType.LESSON && lessonBook) {
            return buildPagesFromBookData(lessonBook);
        }

        return [];
    }, [lessonBook, storyBook, bookType])

    const renderContent = () => {
        if (bookType === BookType.LESSON) {
            if (lessonBookIsLoading) return <Spinner animation="border" />;
            if (lessonBookIsError || !lessonBook)  return <p>Could not load the lesson book. Please try again later.</p>;
        }

        if (bookType === BookType.STORY) {
            if (storyBookIsLoading) return <Spinner animation="border" />;
            if (storyBookIsError || !storyBook) return <p>Could not load the story book. Please try again later.</p>;
        }

        return (
            <HTMLFlipBook
                key={pages.length + JSON.stringify(pages.map((p: React.ReactElement) => p.key || ''))}
                width={450} height={600}
                size="stretch"
                minWidth={315}
                maxWidth={1000}
                minHeight={420}
                maxHeight={1350}
                maxShadowOpacity={0.5}
                showCover={false}
                drawShadow={true}
                flippingTime={1000}
                usePortrait={true}
                startZIndex={0}
                autoSize={true}
                className={styles.flipBook}
                style={{}}
                startPage={0}
                mobileScrollSupport={true}
                clickEventForward={true}
                disableFlipByClick={true}
                useMouseEvents={true}
                swipeDistance={30}
                showPageCorners={true}
            >
                {pages.map((page: React.ReactElement) => page)}
            </HTMLFlipBook>
        );
    }

    return(
        <Modal show={show} onHide={onHide} size="lg" centered>
            <Modal.Header closeButton>
                <Modal.Title>{title || 'Loading...'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {renderContent()}
            </Modal.Body>
            <Modal.Footer>
                <Button className={styles.btnPrimary} onClick={onHide}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    )

};

export default BookReaderModal;