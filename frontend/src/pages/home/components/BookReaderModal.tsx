import {BookType} from "../../../shared/types/types";
import {lessonBookApiSlice, useDeleteLessonBookMutation} from "../../../shared/api/lessonBookApiSlice";
import {storyBookApiSlice, useDeleteStoryBookMutation} from "../../../shared/api/storyBookApiSlice";
import React, {useState} from "react";
import {Button, Modal, Spinner} from "react-bootstrap";
import styles from "./bookreadermodal.module.scss";

import {useAppDispatch} from "../../../app/hooks";
import LessonBookViewer from "../../../features/lessonBook/components/LessonBookViewer";
import FlipBook from "../../../features/storyBook/components/FlipBook";
import {useBookReader} from "./hooks/useBookReader";
import {logToServer} from "../../../shared/utils/loggingService";

interface BookReaderModalProps {
    bookId: number;
    bookType: BookType;
    show: boolean;
    onHide: () => void;
}

const BookReaderModal: React.FC<BookReaderModalProps> = ({ bookId, bookType, show, onHide }) => {
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const dispatch = useAppDispatch();
    const [deleteLessonBook, { isLoading: isDeletingLessonBook }] = useDeleteLessonBookMutation();
    const [deleteStoryBook, { isLoading: isDeletingStoryBook }] = useDeleteStoryBookMutation();

    const { isLoading, isError, lessonBook, storyBook, title } = useBookReader({
        bookId,
        bookType,
        show,
    });

    const handleDeleteClick = () => {
        setShowConfirmDelete(true);
    };

    const confirmDelete = async () => {
        try {
            switch (bookType) {
                case BookType.LESSON:
                    await deleteLessonBook(bookId).unwrap();
                    break;
                case BookType.STORY:
                    await deleteStoryBook(bookId).unwrap();
                    break;
            }
            setShowConfirmDelete(false);
            onHide();
        }
        catch (err) {
            logToServer('error', 'Failed to delete the book: ', err);
            setShowConfirmDelete(false);
        }
    };

    const handleClose = () => {
        /*switch(bookType) {
            case BookType.LESSON:
                dispatch(lessonBookApiSlice.util.invalidateTags([{ type: 'Book', id: bookId }]));
                break;
            case BookType.STORY:
                dispatch(storyBookApiSlice.util.invalidateTags([{ type: 'Book', id: bookId }]));
                break;
        }*/
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
                <Button
                    variant="danger"
                    onClick={handleDeleteClick}
                    disabled={isDeletingLessonBook || isDeletingStoryBook}
                    className="ms-auto">
                    Delete Book
                </Button>
            </Modal.Footer>

            <Modal
                show={showConfirmDelete}
                onHide={() => !(isDeletingLessonBook || isDeletingStoryBook) && setShowConfirmDelete(false)}
                centered
                backdrop="static"
            >
                <Modal.Header closeButton={!(isDeletingLessonBook || isDeletingStoryBook)}>
                    <Modal.Title>Confirm Deletion</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {isDeletingLessonBook || isDeletingStoryBook ? (
                        <div className="text-center">
                            <Spinner animation="border" role="status" className="mb-2" />
                            <p>Deleting book...</p>
                        </div>
                    ) : (
                        <>Are you sure you want to permanently delete <strong>{title || 'this book'}</strong>? This action cannot be undone.</>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirmDelete(false)} disabled={isDeletingLessonBook || isDeletingStoryBook}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={confirmDelete} disabled={isDeletingLessonBook || isDeletingStoryBook}>
                        {isDeletingLessonBook || isDeletingStoryBook ? 'Deleting...' : 'Delete'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Modal>
    )

};

export default BookReaderModal;