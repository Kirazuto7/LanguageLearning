import React from 'react';
import Mascot from './Mascot';
import { useBook } from '../contexts/BookContext';

interface ChapterGeneratorProps {}

const ChapterGenerator: React.FC<ChapterGeneratorProps> = () => {
    const { generateChapter, isLoading, error } = useBook();

    return (
        <div>
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}
            <Mascot onTopicSubmit={generateChapter} isLoading={isLoading} />
        </div>
    );
}

export default ChapterGenerator;