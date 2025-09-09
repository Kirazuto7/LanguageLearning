import React from 'react';
import { Card } from 'react-bootstrap';
import { VocabularyLessonDTO, WordDTO } from '../../../shared/types/dto';
import styles from "./lesson.module.scss";
import { VolumeUpFill } from "react-bootstrap-icons";
import useTextToSpeech from "../../../hooks/useTextToSpeech";
import {useSettingsManager} from "../../userSettings/hooks/useSettingsManager";
import { mascotGenders, MascotName } from "../../../shared/types/types";

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
                {lesson.vocabularies.map((word: WordDTO, index) => {
                    const { details } = word;
                    let nativeWordDisplay: string | JSX.Element = '';
                    let textToSpeak: string = '';

                    if (details?.__typename === 'JapaneseWordDetails') {
                        if (details.kanji && details.hiragana) {
                            nativeWordDisplay = <ruby>{details.kanji}<rt>{details.hiragana}</rt></ruby>;
                        }
                        else {
                            nativeWordDisplay = details.kanji ||details.hiragana || details.katakana || details.romaji || '';
                        }
                        textToSpeak = details.hiragana;
                    }
                    else if (details?.__typename === 'GenericWordDetails') {
                        nativeWordDisplay = details.nativeWord;
                        textToSpeak = details.nativeWord;
                    }

                    return (
                        <div key={word.id} className={styles.vocabCard}>
                            <div className={styles.sentenceNumber}>
                                <div className={styles.circularNumber}>{index + 1}</div>
                            </div>
                            <div className={styles.vocabContent}>
                                <h4 className={styles.vocabWord}>{nativeWordDisplay}</h4>
                                <p className={styles.vocabTranslation}>{word.englishTranslation}</p>
                                {details.__typename === 'JapaneseWordDetails' && (
                                    <div className={styles.vocabDetails}>
                                        {details.kanji && <p><strong>Kanji:</strong> {details.kanji}</p>}
                                        {details.hiragana && <p><strong>Hiragana:</strong> {details.hiragana}</p>}
                                        {details.katakana && <p><strong>Katakana:</strong> {details.katakana}</p>}
                                        {details.romaji && <p><strong>Romaji:</strong> {details.romaji}</p>}
                                    </div>
                                )}
                            </div>
                            <div className={styles.sentenceActions}>
                                <VolumeUpFill
                                    className={styles.speakIcon}
                                    onClick={() => handleSpeak(textToSpeak)}
                                />
                            </div>
                        </div>
                    );
                })}
            </Card.Body>
        </Card>
    );
}
export default VocabularyLesson;