import { useState, useRef } from "react";
import { Button } from "react-bootstrap";
import { Translate } from "react-bootstrap-icons";
import Draggable, { DraggableEvent, DraggableData } from "react-draggable";
import styles from './translationtoolbutton.module.scss';
import { TranslationModal } from "./TranslationModal";

export const TranslationToolButton = () => {
    const [showModal, setShowModal] = useState(false);
    const nodeRef = useRef(null);
    const startPos = useRef({ x: 0, y: 0 });

    const handleClose = () => setShowModal(false);

    const onStartDrag = (e: DraggableEvent, data: DraggableData) => {
        startPos.current = { x: data.x, y: data.y };
    };

    const onStopDrag = (e: DraggableEvent, data: DraggableData) => {
        const dragThreshold = 5;
        const deltaX = Math.abs(data.x - startPos.current.x);
        const deltaY = Math.abs(data.y - startPos.current.y);

        if (deltaX < dragThreshold && deltaY < dragThreshold) {
            setShowModal(true);
        }
    };

    return (
        <>
            <Draggable
                nodeRef={nodeRef}
                onStart={onStartDrag}
                onStop={onStopDrag}
            >
                <Button
                    ref={nodeRef}
                    className={styles.floatingTranslateButton}
                    aria-label="Open Translation Tool (Draggable)"
                >
                    <Translate className={styles.translateIcon} />
                </Button>
            </Draggable>

            <TranslationModal show={showModal} onHide={handleClose} />
        </>
    )
};