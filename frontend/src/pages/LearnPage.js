import ChapterGenerator from "../components/ChapterGenerator";
import LessonBook from "../components/LessonBook";
import FlipBook from "../components/FlipBook";
import React from 'react';
import { LanguageProvider } from "../contexts/LanguageContext";
import { BookProvider } from "../contexts/BookContext";

function LearnPageContent() {
    return (
        <div>
            <FlipBook />
            <ChapterGenerator />
        </div>
    );
}

function LearnPage() {
    return (
        <LanguageProvider>
            <BookProvider>
                <LearnPageContent />
            </BookProvider>
        </LanguageProvider>
    );
}

export default LearnPage;