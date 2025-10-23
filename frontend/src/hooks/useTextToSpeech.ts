import {useState, useEffect, useCallback} from "react";
import { MascotGender } from "../shared/types/types";

interface TextToSpeechHook {
    speak: (text: string, lang: string, gender?: MascotGender) => void;
    cancel: () => void;
    isSpeaking: boolean;
    supported: boolean;
}

const languageCodeMap: { [key: string]: string } = {
    'korean': 'ko-KR',
    'japanese': 'ja-JP',
    'chinese': 'zh-CN',
    'italian': 'it-IT',
    'french': 'fr-FR',
    'spanish': 'es-ES',
    'german': 'de-DE',
    'thai': 'th-TH',
    'english': 'en-US'
};

const preferredVoicesMap: { [key: string]: string[] } = {
    // Japanese Female (prioritizing names that often sound younger)
    'ja-JP-female': ['Sayaka', 'Haruka', 'Ayumi', 'Kyoko'],
    // Japanese Male
    'ja-JP-male': ['Ichiro', 'Otoya'],
    // Korean Female
    'ko-KR-female': ['Google 한국의', 'Heami', 'Yuna'],
    // Korean Male (less common, but we can add known names)
    'ko-KR-male': ['Google 한국의', 'Jimin'],
    // English Female (younger sounding)
    'en-US-female': ['Samantha', 'Google US English', 'Zira'],
    // English Male
    'en-US-male': ['Alex', 'Daniel', 'Google US English'],
};

const useTextToSpeech = (): TextToSpeechHook => {
    const [isSpeaking, setIsSpeaking] = useState(false);
    const [supported, setSupported] = useState(false);
    const [voices, setVoices] = useState<SpeechSynthesisVoice[]>([]);

    useEffect(() => {
        if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
            setSupported(true);

            const handleVoicesChanged = () => {
                setVoices(window.speechSynthesis.getVoices());
                // For debugging: This will show you the exact list of voices your browser has.
                //console.log("Available TTS Voices:", window.speechSynthesis.getVoices());
            };

            window.speechSynthesis.onvoiceschanged = handleVoicesChanged;
            handleVoicesChanged();

            return () => {
                window.speechSynthesis.onvoiceschanged = null;
            };
        }
    }, []);

    const speak = useCallback((text: string, lang: string, gender?: MascotGender) => {
        if (!supported || window.speechSynthesis.speaking) {
            return;
        }
        const langCode = languageCodeMap[lang.toLowerCase()] || 'en-US';
        const utterance = new SpeechSynthesisUtterance(text);

        let selectedVoice: SpeechSynthesisVoice | undefined;

        if (voices.length > 0) {
            const languageVoices = voices.filter(v => v.lang.startsWith(langCode.split('-')[0]));

            if (languageVoices.length > 0) {
                // 1. Try to find a preferred voice from our curated list.
                const preferenceKey = `${langCode}-${gender}`;
                const preferredNames = preferredVoicesMap[preferenceKey] || [];
                for (const name of preferredNames) {
                    selectedVoice = languageVoices.find(v => v.name.includes(name));
                    if (selectedVoice) break;
                }

                // 2. If no preferred voice is found, find the default for the language.
                if (!selectedVoice) {
                    selectedVoice = languageVoices.find(v => v.default);
                }

                // 3. If still no voice, take the first available for the language.
                if (!selectedVoice) {
                    selectedVoice = languageVoices[0];
                }
            }
        }

        //console.log("Selected: " + selectedVoice?.name);
        if (selectedVoice) utterance.voice = selectedVoice;
        utterance.lang = langCode;
        utterance.onstart = () => setIsSpeaking(true);
        utterance.onend = () => setIsSpeaking(false);
        utterance.onerror = () => setIsSpeaking(false);

        window.speechSynthesis.speak(utterance);
    }, [supported, voices]);

    const cancel = useCallback(() => {
        if (!supported) return;
        window.speechSynthesis.cancel();
        setIsSpeaking(false);
    }, [supported]);

    return { speak, cancel, isSpeaking, supported };
}

export default useTextToSpeech;