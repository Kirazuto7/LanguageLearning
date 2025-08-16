import React from "react";
import styles from '../../styles/lessonbook.module.css';
import { Carousel, Spinner } from "react-bootstrap";
import { useLanguage } from "../../contexts/LanguageSettingsContext";
import { useBookManager } from "../../hooks/useBookManager";

interface LessonbookProps{
};

const Lessonbook: React.FC<LessonbookProps> = ({}) => {
    const { language, difficulty } = useLanguage();
    const { pages, title, isLoading } = useBookManager(language, difficulty);

    if (isLoading) {
        return <div className="d-flex justify-content-center align-items-center h-100"><Spinner animation="border" /></div>;
    }

    if (!pages || pages.length === 0) {
        return <div className="text-center h-100 d-flex align-items-center justify-content-center">Select a topic to begin.</div>;
    }

    return(
        <div className="p-3">
            <h2 className="text-center mb-3" style={{color: "white"}}>{title}</h2>
            <Carousel interval={null} indicators={false} className={styles['studybook-carousel']}>
                {pages.map((page: React.ReactElement, index) =>
                    <Carousel.Item key={index} className="h-100" style={{ overflowY: 'auto' }}>
                        {page}
                    </Carousel.Item>
                )}
            </Carousel>
        </div>
    );
}

export default Lessonbook;