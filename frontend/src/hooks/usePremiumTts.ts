import {useCallback, useRef, useEffect} from "react";
import {MascotName} from "../types/types";
import {useSpeakMutation} from "../features/api/ttsApiSlice";

interface PremiumTtsHook {
    speak: (text: string, character: MascotName, language: string) => void;
    cancel: () => void;
    isSpeaking: boolean;
}

const usePremiumTts = (): PremiumTtsHook => {
    const [speakTrigger, { isLoading: isSpeaking, data: audioBlob, reset }] = useSpeakMutation();
    const audioRef = useRef<HTMLAudioElement | null>(null);

    const cancel = useCallback(() => {
        if (audioRef.current) {
            audioRef.current.pause();
            audioRef.current = null;
        }
        reset();
    }, [reset]);

    const speak = useCallback((text: string, character: MascotName, language: string) => {
        if (isSpeaking) {
            cancel();
        }
        speakTrigger({ text, voiceId: character, language });
    }, [isSpeaking, speakTrigger, cancel]);

    useEffect(() => {
        if (audioBlob) {
            const audioUrl = URL.createObjectURL(audioBlob);
            const audio = new Audio(audioUrl);
            audioRef.current = audio;

            audio.play();

            const handleEnd = () => {
                URL.revokeObjectURL(audioUrl);
                reset();
            }

            audio.addEventListener('ended', handleEnd);

            return () => {
                audio.removeEventListener('ended', handleEnd);
                audio.pause();
                URL.revokeObjectURL(audioUrl);
            };
        }
    }, [audioBlob, reset]);

    return { speak, cancel, isSpeaking };
}

export default usePremiumTts;