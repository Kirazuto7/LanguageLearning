import React, { useState } from 'react';
import Mascot from './Mascot';
import { useLanguage } from '../contexts/LanguageSettingsContext';
import { useBook } from '../contexts/BookContext';

function ChapterGenerator() {
    const { language, difficulty } = useLanguage();
    const { processChapter } = useBook();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const generateChapter = async (topic) => {
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
                throw new Error('Failed to generate chapter. The server responded with an error.');
            }

            const data = await response.json();
            console.log('Generated Chapter:', data);
            processChapter(data);
            
        } catch (err) {
            setError(err.message);
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