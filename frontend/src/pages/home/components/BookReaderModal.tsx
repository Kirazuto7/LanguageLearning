import {BookType} from "../../../shared/types/types";
import {lessonBookApiSlice} from "../../../shared/api/lessonBookApiSlice";
import {storyBookApiSlice} from "../../../shared/api/storyBookApiSlice";
import React, {useState} from "react";
import {Button, Modal, Spinner} from "react-bootstrap";
import styles from "./bookreadermodal.module.scss";

import {useAppDispatch} from "../../../app/hooks";
import LessonBookViewer from "../../../features/lessonBook/components/LessonBookViewer";
import FlipBook from "../../../features/storyBook/components/FlipBook";
import { useBookReader } from "./hooks/useBookReader";

interface BookReaderModalProps {
    bookId: number;
    bookType: BookType;
    show: boolean;
    onHide: () => void;
}

const BookReaderModal: React.FC<BookReaderModalProps> = ({ bookId, bookType, show, onHide }) => {
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);
    const dispatch = useAppDispatch();

    const { isLoading, isError, lessonBook, storyBook, title } = useBookReader({
        bookId,
        bookType,
        show,
    });

    const handleClose = () => {
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
        if (isLoading) {
            return <Spinner animation="border" />;
        }
        if (isError) {
            return <p>Could not load the book. Please try again later.</p>;
        }

        if (bookType === BookType.LESSON && lessonBook) {
            return (
                <LessonBookViewer
                    title={lessonBook.title}
                    chapters={lessonBook.lessonChapters}
                    activeChapterIndex={activeChapterIndex}
                    setActiveChapterIndex={setActiveChapterIndex}
                />
            );
        }

        if (bookType === BookType.STORY && storyBook) {
            return (
                <FlipBook stories={storyBook.shortStories} title={storyBook.title} />
            );
        }
        return <></>;
    }
    return(
        <Modal show={show} onHide={handleClose} size="xl" centered>
            <Modal.Header closeButton>
                <Modal.Title>{title || 'Loading...'}</Modal.Title>
            </Modal.Header>
            <Modal.Body className={styles.modalBody}>
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