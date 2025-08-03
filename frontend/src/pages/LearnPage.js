import ChapterGenerator from "../components/ChapterGenerator";
import LessonBook from "../components/LessonBook";
import React from 'react';

function LearnPage() {
    return (
        <div>
            <h1>Learn Page</h1>
            <LessonBook/>
            <ChapterGenerator />
        </div>
    );
}

export default LearnPage;