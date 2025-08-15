import ChapterGenerator from "../components/ChapterGenerator";
import FlipBook from "../components/studybook/FlipBook";
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
            <FlipBook
                generatedChapterPage={generatedChapterPage}
                onFlipComplete={() => setGeneratedChapterPage(null)}
            />
            <ChapterGenerator onChapterGenerated={setGeneratedChapterPage} />
        </div>
    );
}

export default StudyBookPage;