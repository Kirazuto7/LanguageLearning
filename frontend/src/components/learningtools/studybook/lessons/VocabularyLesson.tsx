import React from 'react';
import { Card } from 'react-bootstrap';
import { VocabularyLessonDTO, WordDTO } from '../../../../types/dto';
import styles from "./lesson.module.scss";
import { VolumeUpFill } from "react-bootstrap-icons";
import useTextToSpeech from "../../../../hooks/useTextToSpeech";
import {useSettingsManager} from "../../../../hooks/useSettingsManager";
import { mascotGenders, MascotName } from "../../../../types/types";

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();

    const isJapanese = lesson.vocabularies.length > 0 && lesson.vocabularies[0].language.toLowerCase() === 'japanese';

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
                    <div key={word.id} className={styles.vocabCard}>
                        <div className={styles.sentenceNumber}>
                            <div className={styles.circularNumber}>{index + 1}</div>
                        </div>
                        <div className={styles.vocabContent}>
                            {isJapanese ? (
                                <h4 className={styles.vocabWord}>
                                    <ruby className={styles.vocabRuby}>
                                        {word.details?.kanji || word.nativeWord}
                                        <rt>{word.details?.hiragana}</rt>
                                    </ruby>
                                </h4>
                            ) : (
                                <h4 className={styles.vocabWord}>{word.nativeWord}</h4>
                            )}
                            <p className={styles.vocabTranslation}>{word.englishTranslation}</p>
                        </div>
                        <div className={styles.sentenceActions}>
                            <VolumeUpFill
                                className={styles.speakIcon}
                                onClick={() => handleSpeak(word.nativeWord)}
                            />
                        </div>
                    </div>
                ))}
            </Card.Body>
        </Card>
    );
}
export default VocabularyLesson;
