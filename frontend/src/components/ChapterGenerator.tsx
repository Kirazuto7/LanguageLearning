import React, { useState } from 'react';
import Mascot from './Mascot';
import { useLanguage } from '../contexts/LanguageSettingsContext';
import { useBook } from '../contexts/BookContext';
import { ChapterDTO } from '../types/dto';

interface ChapterGeneratorProps {

}

const ChapterGenerator: React.FC<ChapterGeneratorProps> = () => {
    const { language, difficulty } = useLanguage();
    const { processChapter } = useBook();
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const generateChapter = async (topic: string) => {
        setIsLoading(true);
        setError(null);
        
        try {
            const response = await fetch('/api/chapters/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ language, difficulty, topic }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to generate chapter. The server responded with an error.');
            }

            const data: ChapterDTO = await response.json();
            console.log('Generated Chapter:', data);
            processChapter(data);
            
        } catch (err) {
            if(err instanceof Error)
                setError(err.message);
            else
                setError('An unknown error occurred.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}
            <Mascot onTopicSubmit={generateChapter} />
        </div>
    );
}

export default ChapterGenerator;