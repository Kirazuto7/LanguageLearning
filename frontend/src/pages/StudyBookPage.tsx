import ChapterGenerator from "../components/ai/ChapterGenerator";
import Lessonbook from "../components/learningtools/studybook/Lessonbook";
import React, { useState } from 'react';
import { useLocation, Navigate } from "react-router-dom";

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {

    const location = useLocation();
    const { language, difficulty } = location.state || {};
    const [generatedChapterPage, setGeneratedChapterPage] = useState<number | null>(null);

    if(!language || !difficulty) {
        return <Navigate to="/home" />;
    }

    return (
        <div>
            <Lessonbook/>
            
            <ChapterGenerator onChapterGenerated={setGeneratedChapterPage} />
        </div>
    );
}

export default StudyBookPage;

/*<FlipBook
                generatedChapterPage={generatedChapterPage}
                onFlipComplete={() => setGeneratedChapterPage(null)}
            />*/