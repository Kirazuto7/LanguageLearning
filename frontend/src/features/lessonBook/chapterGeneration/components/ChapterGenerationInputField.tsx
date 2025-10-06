import React, { useState } from "react";
import styles from "../../../../shared/components/mascot/mascot.module.scss";
import chapterStyles from "./chapterGenerationInputField.module.scss";

interface ChapterGenerationInputFieldProps {
    onSend: (value:string) => void;
    disabled?: boolean;
}
const ChapterGenerationInputField: React.FC<ChapterGenerationInputFieldProps> = ({onSend, disabled = false }) => {
    const [inputValue, setInputValue] = useState<string>('');

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if(onSend && inputValue.trim() && !disabled) {
            onSend(inputValue);
            setInputValue('');
        }
    }

    return(
        <form id={styles.chatForm} className={chapterStyles.inputForm} onSubmit={handleSubmit}>
            <input
                type="text"
                id={styles.userInput}
                placeholder="Suggest a topic..."
                className={`${styles.textInput} ${chapterStyles.textInput}`}
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                disabled={disabled}
            />
            <button type="submit" id={styles.sendButton} className={`${styles.sendButton} btn btn-primary`} disabled={disabled}>
                {disabled ? 'Generating...' : 'Send'}
            </button>
        </form>
    )
}

export default ChapterGenerationInputField;