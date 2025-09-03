import Lessonbook from "../../components/learningtools/studybook/Lessonbook";
import React, {useCallback, useEffect, useState} from 'react';
import { useStudyBookManager } from "../../hooks/useStudyBookManager";
import { Container, Spinner, Alert } from "react-bootstrap";
import { useChapterGeneration } from "../../hooks/useChapterGeneration";
import StudyBookMascot from "../../components/ai/StudyBookMascot";

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    const { title, chapters, isLoading: isBookLoading, error: bookError, language, difficulty } = useStudyBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);
    const [celebrationTrigger, setCelebrationTrigger] = useState(0);

    const {
        startGeneration,
        isLoading: isGenerating,
        progress,
        message,
        error: generationError,
        isComplete
    } = useChapterGeneration(language, difficulty);

    // For Practice Lesson Callback Function
    const handleAllCorrect = useCallback(() => {
        setCelebrationTrigger(prev => prev + 1);
    }, []);

    const isInitialLoading = isBookLoading && chapters.length === 0;

    useEffect(() => {
        if (chapters.length > 0) {
            setActiveChapterIndex(chapters.length -1 );
        }
    }, [chapters.length]);

    return (
        <div>
            {isInitialLoading ? (
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
                    <Container className="mt-4">
                        { generationError && <Alert variant="danger" className="mt-2">{generationError}</Alert>}
                    </Container>

                    <StudyBookMascot
                        celebrationTrigger={celebrationTrigger}
                        onTopicSubmit={startGeneration}
                        isLoading={isGenerating}
                        progress={progress}
                        message={message}
                    />
                </>
            )}
        </div>
    );
}

export default StudyBookPage;
