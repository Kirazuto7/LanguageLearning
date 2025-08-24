import ChapterGenerator from "../../components/ai/ChapterGenerator";
import Lessonbook from "../../components/learningtools/studybook/Lessonbook";
import React, {useEffect, useState} from 'react';
import {useStudyBookManager} from "../../hooks/useStudyBookManager";

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    const { title, chapters, isLoading } = useStudyBookManager();
    const [activeChapterIndex, setActiveChapterIndex] = useState<number>(0);

    useEffect(() => {
        if (chapters.length > 0) {
            setActiveChapterIndex(chapters.length -1 );
        }
    }, [chapters]);


    return (
        <div>
            <Lessonbook
                title={title}
                chapters={chapters}
                isLoading={isLoading}
                activeChapterIndex={activeChapterIndex}
                setActiveChapterIndex={setActiveChapterIndex}
            />
            
            <ChapterGenerator/>
        </div>
    );
}

export default StudyBookPage;
