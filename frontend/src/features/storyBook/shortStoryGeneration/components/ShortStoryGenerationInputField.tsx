import React, { useState } from "react";
import styles from "../../../../shared/components/mascot/mascot.module.scss";
import { Form } from 'react-bootstrap';

export interface StoryGenerationInput {
    topic: string;
    genre: string;
}

interface ShortStoryGenerationInputFieldProps {
    onSend: (input: StoryGenerationInput) => void;
    disabled?: boolean;
}

const storyGenres = [
    'Fantasy',
    'Sci-Fi',
    'Mystery',
    'Adventure',
    'Comedy',
    'Horror',
    'Romance',
    'Thriller'
];

const ShortStoryGenerationInputField: React.FC<ShortStoryGenerationInputFieldProps> = ({ onSend, disabled = false }) => {
    const [topic, setTopic] = useState<string>('');
    const [genre, setGenre] = useState<string>(storyGenres[0]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (onSend && topic.trim() && genre && !disabled) {
            onSend({ topic, genre });
            setTopic('');
        }
    }

    return (
        <form id={styles.chatForm} className={`${styles.inputArea} d-flex align-items-center`} onSubmit={handleSubmit}>
            <Form.Select
                value={genre}
                onChange={(e) => setGenre(e.target.value)}
                disabled={disabled}
                className={`${styles.genreSelect} me-2`}
                aria-label="Select story genre"
            >
                {storyGenres.map(g => <option key={g} value={g}>{g}</option>)}
            </Form.Select>
            <input
                type="text"
                id={styles.userInput}
                placeholder="Suggest a story topic..."
                className={styles.textInput}
                value={topic}
                onChange={(e) => setTopic(e.target.value)}
                disabled={disabled}
            />
            <button type="submit" id={styles.sendButton} className={`${styles.sendButton} btn btn-primary`} disabled={disabled}>
                {disabled ? 'Generating...' : 'Generate'}
            </button>
        </form>
    )
}

export default ShortStoryGenerationInputField;