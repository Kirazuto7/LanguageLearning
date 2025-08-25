import ChapterGenerator from "../../components/ai/ChapterGenerator";
import Lessonbook from "../../components/learningtools/studybook/Lessonbook";
import React, {useEffect, useState} from 'react';
import {useStudyBookManager} from "../../hooks/useStudyBookManager";
import { Container, Spinner } from "react-bootstrap";

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    const { title, chapters, isLoading } = useStudyBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);

    useEffect(() => {
        if (chapters.length > 0) {
            setActiveChapterIndex(chapters.length -1 );
        }
    }, [chapters]);

    if (isLoading && chapters.length === 0) {
        return(
            <Container className="d-flex justify-content-center align-items-center" style={{minHeight: '80vh'}}>
                <Spinner animation="border"/>
            </Container>
        )
    }


    return (
        <div>
            <Lessonbook
                title={title}
                chapters={chapters}
                activeChapterIndex={activeChapterIndex}
                setActiveChapterIndex={setActiveChapterIndex}
            />
            
            <ChapterGenerator/>
        </div>
    );
}

export default StudyBookPage;
