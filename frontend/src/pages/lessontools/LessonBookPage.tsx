import LessonBookViewer from "../../features/lessonBook/components/LessonBookViewer";
import React, {useCallback, useEffect, useState} from 'react';
import {useLessonBookManager} from "../../features/lessonBook/hooks/useLessonBookManager";
import {Container, Spinner} from "react-bootstrap";
import {useChapterGeneration} from "../../features/lessonBook/chapterGeneration/hooks/useChapterGeneration";
import LessonBookMascot from "../../widgets/lessonBookMascot/LessonBookMascot";

interface StudyBookPageProps{}

const LessonBookPage: React.FC<StudyBookPageProps> = () => {
    const { title, chapters, isLoading: isBookLoading, language, difficulty } = useLessonBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);
    const [celebrationTrigger, setCelebrationTrigger] = useState(0);

    const {
        startGeneration,
        isLoading: isGenerating,
        progress,
        message,
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
                    <LessonBookViewer
                        title={title}
                        chapters={chapters}
                        activeChapterIndex={activeChapterIndex}
                        setActiveChapterIndex={setActiveChapterIndex}
                        onAllCorrect={handleAllCorrect}
                        isGenerating={isGenerating}
                    />

                    <LessonBookMascot
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

export default LessonBookPage;
