import ChapterGenerator from "../components/ChapterGenerator";
import FlipBook from "../components/studybook/FlipBook";
import React from 'react';

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    return (
        <div>
            <FlipBook />
            <ChapterGenerator />
        </div>
    );
}

export default StudyBookPage;