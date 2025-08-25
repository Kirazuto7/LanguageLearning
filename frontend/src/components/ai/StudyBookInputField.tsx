import React, {useEffect, useState} from "react";
import styles from "./mascot.module.scss";

interface StudyBookInputFieldProps {
    onSend: (value:string) => void;
    disabled?: boolean;
}
const StudyBookInputField: React.FC<StudyBookInputFieldProps> = ({onSend, disabled = false }) => {
    const [inputValue, setInputValue] = useState<string>('');

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if(onSend && inputValue.trim() && !disabled) {
            onSend(inputValue);
            setInputValue('');
        }
    }

    return(
        <form id={styles.chatForm} className={styles.inputArea} onSubmit={handleSubmit}>
            <input
                type="text"
                id={styles.userInput}
                placeholder="Suggest a topic..."
                className={styles.textInput}
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

export default StudyBookInputField;