import React, { useState } from 'react';
import Mascot from './Mascot';

function ChapterGenerator() {
    const [language, setLanguage] = useState('Korean');
    const [level, setLevel] = useState('Beginner');
    const [generatedChapter, setGeneratedChapter] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [openSettings, setOpenSettings] = useState(false);

    const generateChapter = async (topic) => {
        setIsLoading(true);
        setError(null);
        setGeneratedChapter(null);
        
        try {
            const response = await fetch('/api/chapters/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ language, level, topic }),
            });

            if (!response.ok) {
                throw new Error('Failed to generate chapter. The server responded with an error.');
            }

            const data = await response.json();
            console.log('Generated Chapter:', data);
            setGeneratedChapter(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}

            {generatedChapter && (
                <div>
                    <h2>{generatedChapter.title} ({generatedChapter.korean_title})</h2>
                    {/* You would map over generatedChapter.lessons and render different components based on lesson.type */}
                </div>
            )}

            <Mascot
                onTopicSubmit={generateChapter}
                language={language}
                setLanguage={setLanguage}
                level={level}
                setLevel={setLevel}
                openSettings={openSettings}
                setOpenSettings={setOpenSettings}
            />
        </div>
    );
}

export default ChapterGenerator;