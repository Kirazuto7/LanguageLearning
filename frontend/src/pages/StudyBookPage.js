import ChapterGenerator from "../components/ChapterGenerator";
import FlipBook from "../components/studybook/FlipBook";
import React from 'react';


function StudyBookPage() {
    return (
        <div>
            <FlipBook />
            <ChapterGenerator />
        </div>
    );
}

export default StudyBookPage;