import ChapterGenerator from "../components/ai/ChapterGenerator";
import Lessonbook from "../components/learningtools/studybook/Lessonbook";
import React, { useState } from 'react';

interface StudyBookPageProps{}

const StudyBookPage: React.FC<StudyBookPageProps> = () => {
    const [generatedChapterPage, setGeneratedChapterPage] = useState<number | null>(null);

    return (
        <div>
            <Lessonbook/>
            
            <ChapterGenerator onChapterGenerated={setGeneratedChapterPage} />
        </div>
    );
}

export default StudyBookPage;
