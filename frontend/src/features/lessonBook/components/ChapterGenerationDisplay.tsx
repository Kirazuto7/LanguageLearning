import React from "react";
import { Spinner, Container } from "react-bootstrap";
import styles from './chapterGenerationDisplay.module.scss';

const ChapterGenerationDisplay: React.FC = () => {
    return(
        <Container className={`${styles.generationContainer} glass-container text-center`}>
            <p className={`mt-3 mb-1 ${styles.mainText}`}>Your personalized chapter is being created!</p>
            <p className={styles.subText}>Please watch a short video to support our service while the AI works its magic.</p>
            <Spinner animation="border" role="status" className={`mt-4 ${styles.largeSpinner}`} />
        </Container>
    )
}

export default ChapterGenerationDisplay;