import React from 'react';
import { Mascot } from './Mascot';
import { useStudyBookManager } from '../../hooks/useStudyBookManager';

interface ChapterGeneratorProps {
    onChapterGenerated: (pageNumber: number) => void;
}

const ChapterGenerator: React.FC<ChapterGeneratorProps> = ({
    onChapterGenerated
}) => {
    const { generateChapter, isLoading, error } = useStudyBookManager();

    const handleTopicSubmit = async (topic: string) => {
        const newChapter = await generateChapter(topic);

        if(newChapter && newChapter.pages.length > 0) {
            const pageIndexToFlipTo = newChapter.pages[0].pageNumber - 1;
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