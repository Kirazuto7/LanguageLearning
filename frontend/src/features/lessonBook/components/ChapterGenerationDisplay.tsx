import React from "react";
import { Spinner, Container } from "react-bootstrap";
import styles from './chapterGenerationDisplay.module.scss';

const ChapterGenerationDisplay: React.FC = () => {
    return(
        <div className={styles.outerWrapper}>
            <Container className={`${styles.generationContainer} text-center`}>
                <div className={`${styles.innerContainer} glass-container`}>
                    <div className={styles.contentWrapper}>
                        <p className={`mt-3 mb-1 ${styles.mainText}`}>Your personalized chapter is being created!</p>
                        <p className={styles.subText}>Est. time: 4-5 minutes. Please watch a short video to support our service while the AI works its magic.</p>
                        <Spinner animation="border" role="status" className={`mt-4 ${styles.largeSpinner}`} />
                    </div>
                </div>
            </Container>
        </div>
    )
}

export default ChapterGenerationDisplay;