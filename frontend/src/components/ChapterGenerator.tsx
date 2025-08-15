import React from 'react';
import { Mascot } from './Mascot';
import { useBookManager } from '../hooks/useBookManager';
import { useLanguage } from '../contexts/LanguageSettingsContext';

interface ChapterGeneratorProps {
    onChapterGenerated: (pageNumber: number) => void;
}

const ChapterGenerator: React.FC<ChapterGeneratorProps> = ({ onChapterGenerated }) => {
    const { language, difficulty } = useLanguage();
    const { generateChapter, isLoading, error } = useBookManager(language, difficulty);

    const handleTopicSubmit = async (topic: string) => {
        const newChapter = await generateChapter(topic);

        if(newChapter && newChapter.pages.length > 0) {
            const pageIndexToFlipTo = newChapter.pages[0].pageNumber + 2; // Factoring in the initial cover & toc page
            onChapterGenerated(pageIndexToFlipTo);
        }
    }

    return (
        <div>
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}
            <Mascot onTopicSubmit={handleTopicSubmit} isLoading={isLoading} />
        </div>
    );
}

export default ChapterGenerator;