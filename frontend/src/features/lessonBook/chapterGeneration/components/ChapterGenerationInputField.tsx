import React, {useState} from "react";
import styles from "../../../../shared/components/mascot/mascot.module.scss";
import chapterStyles from "./chapterGenerationInputField.module.scss";
import {isTextProfane} from "../../../../shared/utils/textUtils";
import {useAlert} from "../../../../shared/contexts/AlertContext";
import {AlertLevel} from "../../../../shared/types/types";

interface ChapterGenerationInputFieldProps {
    onSend: (value:string) => void;
    disabled?: boolean;
}
const ChapterGenerationInputField: React.FC<ChapterGenerationInputFieldProps> = ({onSend, disabled = false }) => {
    const [inputValue, setInputValue] = useState<string>('');
    const [isInputProfane, setIsInputProfane] = useState<boolean>(false);
    const { showAlert } = useAlert();

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if(onSend && inputValue.trim() && !disabled && !isInputProfane) {
            onSend(inputValue);
            setInputValue('');
        }
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        setInputValue(newValue);
        const isProfane = isTextProfane(newValue);
        setIsInputProfane(isProfane);

        if (isProfane) {
            showAlert("This topic is not supported.", AlertLevel.WARN);
        }
    };

    return(
        <form id={styles.chatForm} className={chapterStyles.inputForm} onSubmit={handleSubmit}>
            <input
                type="text"
                id={styles.userInput}
                placeholder="Suggest a topic..."
                className={`${styles.textInput} ${chapterStyles.textInput}`}
                value={inputValue}
                onChange={handleInputChange}
                disabled={disabled}
            />
            <button type="submit" id={styles.sendButton} className={`${styles.sendButton} btn btn-primary`} disabled={disabled || isInputProfane}>
                {disabled ? 'Generating...' : 'Send'}
            </button>
        </form>
    )
}

export default ChapterGenerationInputField;