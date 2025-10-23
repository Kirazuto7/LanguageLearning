import React from 'react';
import { Card } from 'react-bootstrap';
import { VocabularyLessonDTO, WordDTO } from '../../../shared/types/dto';
import useTextToSpeech from "../../../hooks/useTextToSpeech";
import {useSettingsManager} from "../../userSettings/hooks/useSettingsManager";
import { mascotGenders, MascotName } from "../../../shared/types/types";
import WordCard from "./common/WordCard";

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();

    const handleSpeak = (text: string) => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            cancel();
            speak(text, settings.language, gender);
        }
    };

    return(
        <Card className="h-100 d-flex flex-column">
            <Card.Header as="h4" className="text-center">
                {lesson.title}
            </Card.Header>

            <Card.Body style={{ overflowY: 'auto' }} className="p-4">
                {lesson.vocabularies.map((word: WordDTO, index) => (
                    <WordCard
                        key={word.id}
                        word={word}
                        index={index}
                        onSpeak={handleSpeak}
                    />
                ))}
            </Card.Body>
        </Card>
    );
}
export default VocabularyLesson;