import React, { useState } from 'react';

function ChapterGenerator() {
    const [language, setLanguage] = useState('Korean');
    const [topic, setTopic] = useState('');
    const [generatedChapter, setGeneratedChapter] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);
        setGeneratedChapter(null);

        try {
            const response = await fetch('/api/chapters/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ language, topic }),
            });

            if (!response.ok) {
                throw new Error('Failed to generate chapter. The server responded with an error.');
            }

            const data = await response.json();
            setGeneratedChapter(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <h1>Generate a New Language Chapter</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="language-select">Language:</label>
                    <select id="language-select" value={language} onChange={(e) => setLanguage(e.target.value)}>
                        <option value="Korean">Korean</option>
                        <option value="Japanese">Japanese</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="topic-input">Chapter Topic:</label>
                    <input
                        id="topic-input"
                        type="text"
                        value={topic}
                        onChange={(e) => setTopic(e.target.value)}
                        placeholder="e.g., Ordering coffee"
                        required
                    />
                </div>
                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Generating...' : 'Generate Chapter'}
                </button>
            </form>

            {error && <p style={{ color: 'red' }}>Error: {error}</p>}

            {generatedChapter && (
                <div>
                    <h2>{generatedChapter.title} ({generatedChapter.korean_title})</h2>
                    {/* You would map over generatedChapter.lessons and render different components based on lesson.type */}
                </div>
            )}
        </div>
    );
}

export default ChapterGenerator;