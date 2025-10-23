import { useState, useCallback } from 'react';
import leoProfanity from 'leo-profanity';
import { useAlert } from "../shared/contexts/AlertContext";
import { AlertLevel } from "../shared/types/types";

const DEFAULT_PROFANITY_MESSAGE = "This input is not supported.";

// Initialize leo-profanity once when the module is loaded.
// Load English dictionary by default
leoProfanity.loadDictionary();

// Add additional words for other languages (extend as needed)
leoProfanity.add([
    // Korean
    '시발', '병신', '개새끼', 'ㅅㅂ',
    // Japanese
    'くそ', '死ね', 'アホ', 'ちくしょう',
    // Thai
    'สัส', 'เหี้ย', 'ควย', 'แม่ง', 'โง่',
    // Chinese
    '他妈的', '傻逼', '滚蛋', '妈的', '操你',
    // Additional European words if needed
    'scheiße', 'merde', 'cazzo'
]);

/**
 * A custom hook to handle profanity checking for text input.
 * It encapsulates the logic for checking text and showing an alert.
 * @returns An object containing the profane status and a function to check text.
 */
export const useProfanityCheck = () => {
    const [isProfane, setIsProfane] = useState(false);
    const { showAlert } = useAlert();

    const checkText = useCallback((text: string) => {
        const profane = leoProfanity.check(text);
        setIsProfane(profane);
        if (profane) {
            showAlert(DEFAULT_PROFANITY_MESSAGE, AlertLevel.WARN);
        }
    }, [showAlert]);

    return { isProfane, checkText };
};