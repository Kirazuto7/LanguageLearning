import ChapterGenerator from "../../components/ai/ChapterGenerator";
import Lessonbook from "../../components/learningtools/studybook/Lessonbook";
import React, {useCallback, useEffect, useState} from 'react';
import {useStudyBookManager} from "../../hooks/useStudyBookManager";
import { Container, Spinner } from "react-bootstrap";

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    const { title, chapters, isLoading } = useStudyBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);
    const [celebrationTrigger, setCelebrationTrigger] = useState(0);

    // For Practice Lesson Callback Function
    const handleAllCorrect = useCallback(() => {
        setCelebrationTrigger(prev => prev + 1);
    }, []);

    useEffect(() => {
        if (chapters.length > 0) {
            setActiveChapterIndex(chapters.length -1 );
        }
    }, [chapters]);

    return (
        <div>
            {isLoading && chapters.length === 0 ? (
                <Container className="d-flex justify-content-center align-items-center" style={{minHeight: '80vh'}}>
                    <Spinner animation="border"/>
                </Container>
            ) : (
                <>
                    <Lessonbook
                        title={title}
                        chapters={chapters}
                        activeChapterIndex={activeChapterIndex}
                        setActiveChapterIndex={setActiveChapterIndex}
                        onAllCorrect={handleAllCorrect}
                    />
                    <ChapterGenerator celebrationTrigger={celebrationTrigger}/>
                </>
            )}
        </div>
    );
}

export default StudyBookPage;
