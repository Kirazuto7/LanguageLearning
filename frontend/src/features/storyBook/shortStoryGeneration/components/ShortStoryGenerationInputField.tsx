import React, { useState } from "react";
import styles from "../../../../shared/components/mascot/mascot.module.scss";
import storyStyles from "./shortStoryGenerationInputField.module.scss";
import { Form } from 'react-bootstrap';
import { isTextProfane } from "../../../../shared/utils/textUtils";
import { useAlert } from "../../../../shared/contexts/AlertContext";
import { AlertLevel } from "../../../../shared/types/types";

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
    const [isTopicProfane, setIsTopicProfane] = useState<boolean>(false);
    const { showAlert } = useAlert();

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (onSend && topic.trim() && genre && !disabled && !isTopicProfane) {
            onSend({ topic, genre });
            setTopic('');
        }
    }

    const handleTopicChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newTopic = e.target.value;
        setTopic(newTopic);

        const isProfane = isTextProfane(newTopic);
        setIsTopicProfane(isProfane);

        if (isProfane) {
            showAlert("This topic is not supported.", AlertLevel.WARN);
        }
    }

    return (
        <form id={styles.chatForm} className={storyStyles.inputForm} onSubmit={handleSubmit}>
            <Form.Select
                value={genre}
                onChange={(e) => setGenre(e.target.value)}
                disabled={disabled}
                className={`${storyStyles.genreSelect} me-2`}
                aria-label="Select story genre"
            >
                {storyGenres.map(g => <option key={g} value={g}>{g}</option>)}
            </Form.Select>
            <input
                type="text"
                id={styles.userInput}
                placeholder="Suggest a story topic..."
                className={`${styles.textInput} ${storyStyles.storyInput}`}
                value={topic}
                onChange={handleTopicChange}
                disabled={disabled}
            />
            <button type="submit" id={styles.sendButton} className={`${styles.sendButton} ${storyStyles.storyButton} btn btn-primary`} disabled={disabled || isTopicProfane}>
                {disabled ? 'Generating...' : 'Generate'}
            </button>
        </form>
    )
}

export default ShortStoryGenerationInputField;