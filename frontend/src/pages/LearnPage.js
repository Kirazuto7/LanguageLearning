import ChapterGenerator from "../components/ChapterGenerator";
import FlipBook from "../components/FlipBook";
import React from 'react';


function LearnPage() {
    return (
        <div>
            <FlipBook />
            <ChapterGenerator />
        </div>
    );
}

export default LearnPage;