import {BookType} from "../../../shared/types/types";
import {lessonBookApiSlice, useLazyGetLessonBookByIdQuery} from "../../../shared/api/lessonBookApiSlice";
import {storyBookApiSlice, useLazyGetStoryBookByIdQuery} from "../../../shared/api/storyBookApiSlice";
import React, {useEffect, useMemo} from "react";
import {Button, Modal, Spinner} from "react-bootstrap";
import {buildPagesFromStoryData} from "../../../features/storyBook/utils/buildPagesFromStoryData";
import styles from "./bookreadermodal.module.scss";
import HTMLFlipBook from "react-pageflip";
import {buildPagesFromBookData} from "../../../features/lessonBook/utils/buildPagesFromLessonData";
import {useAppDispatch} from "../../../app/hooks";
import {logToServer, safeToString} from "../../../shared/utils/loggingService";

interface BookReaderModalProps {
    bookId: number;
    bookType: BookType;
    bookColor: string;
    show: boolean;
    onHide: () => void;
}

const BookReaderModal: React.FC<BookReaderModalProps> = ({ bookId, bookType, bookColor, show, onHide }) => {
    const [triggerGetLessonBook, { data: lessonBook, isFetching: lessonBookIsFetching, isError: lessonBookIsError }] = useLazyGetLessonBookByIdQuery();
    const [triggerGetStoryBook, { data: storyBook, isFetching: storyBookIsFetching, isError: storyBookIsError }] = useLazyGetStoryBookByIdQuery();
    const title = bookType === BookType.LESSON ? lessonBook?.title : storyBook?.title;
    const dispatch = useAppDispatch();
    const bookColorStyle = {
        [bookType === BookType.LESSON ? '--book-cover-color' : '--storybook-cover-bg']: bookColor,
    } as React.CSSProperties;

    useEffect(() => {
        if (!show || !bookId) return;

        if (show) {
            switch (bookType) {
                case BookType.LESSON:
                    triggerGetLessonBook(bookId);
                    break;
                case BookType.STORY:
                    triggerGetStoryBook(bookId);
                    break;
            }
        }
    }, [show, bookId, bookType, triggerGetLessonBook, triggerGetStoryBook]);

    const pages = useMemo(() => {
        switch(bookType) {
            case BookType.LESSON:
                return lessonBook ? buildPagesFromBookData(lessonBook) : [];
            case BookType.STORY:
                return storyBook ? buildPagesFromStoryData(storyBook.shortStories) : [];
            default:
                return [];
        }
    }, [lessonBook, storyBook, bookType])

    const handleClose = () => {
        console.log("called");

        switch(bookType) {
            case BookType.LESSON:
                dispatch(lessonBookApiSlice.util.invalidateTags([{ type: 'Book', id: bookId }]));
                break;
            case BookType.STORY:
                dispatch(storyBookApiSlice.util.invalidateTags([{ type: 'Book', id: bookId }]));
                break;
        }
        onHide();
    }

    const renderContent = () => {
        let isFetching, isError, data;
        switch (bookType) {
            case BookType.LESSON:
                isFetching = lessonBookIsFetching;
                isError = lessonBookIsError;
                data = lessonBook;
                logToServer('debug', "Book Data:", safeToString(data));
                break;
            case BookType.STORY:
                isFetching = storyBookIsFetching;
                isError = storyBookIsError;
                data = storyBook;
                logToServer('debug', "Book Data:", safeToString(data));
                break;
            default:
                return <p>Invalid book type selected.</p>;
        }

        if (isFetching) return <Spinner animation="border" />;
        if (isError || !data) return <p>Could not load the book. Please try again later.</p>;
        logToServer('debug', "# Pages: ", safeToString(pages?.length));

        return (
        <div className={styles.bookContainer}>
            <div className={styles.bookBinding}></div>
            <HTMLFlipBook
                key={pages.length + JSON.stringify(pages.map((p: React.ReactElement) => p.key || '')) + bookId + bookType}
                width={400} height={550}
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
                {pages && pages.filter(Boolean).map((page: React.ReactElement) => page)}

                {pages && pages.length % 2 !== 0 &&
                     <div className={styles.backcover} style={bookColorStyle}>
                        <h2 className={`mt-5 text-center ${styles['book-title']}`}>The End</h2>
                    </div>
                }
            </HTMLFlipBook>
        </div>
        );
    }
    return(
        <Modal show={show} onHide={handleClose} size="xl" centered>
            <Modal.Header closeButton>
                <Modal.Title>{title || 'Loading...'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {renderContent()}
            </Modal.Body>
            <Modal.Footer>
                <Button className={styles.btnPrimary} onClick={handleClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    )

};

export default BookReaderModal;