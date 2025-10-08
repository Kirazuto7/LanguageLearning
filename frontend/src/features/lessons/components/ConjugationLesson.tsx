import React from 'react';
import { ConjugationLessonDTO, ConjugationExampleDTO } from '../../../shared/types/dto';
import parse from 'html-react-parser';
import { Card } from 'react-bootstrap';
import styles from './lesson.module.scss';
import { useSettingsManager } from '../../userSettings/hooks/useSettingsManager';
import useTextToSpeech from "../../../hooks/useTextToSpeech";
import {mascotGenders, MascotName} from "../../../shared/types/types";
import { VolumeUpFill } from "react-bootstrap-icons";
import { getPhoneticText } from '../../../shared/utils/textUtils';

interface ConjugationLessonProps {
    lesson: ConjugationLessonDTO;
}

/**
 * Renders the content for a conjugation grammar lesson, displaying the conjugation table & example sentences.
*/
const ConjugationLesson: React.FC<ConjugationLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const handleSpeak = (text: string) => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            const textToSpeech = isJapanese ? getPhoneticText(text) : text;
            cancel();
            speak(textToSpeech, settings.language, gender);
        }
    };

    const renderText = (text: string, { as: Component = 'div' as React.ElementType, className = '' } = {}) => {
        if (!text || text.length === 0) return <></>;
        return <Component className={className}>{parse(text)}</Component>;
    };

    return (
        <div>
            <h2 className="text-center mb-4">{lesson.title}</h2>
            
            <h6>{lesson.conjugationRuleName}</h6>
            <h6>{lesson.explanation}</h6>
            {
                lesson.conjugatedWords.map((word: ConjugationExampleDTO, index) => (
                    <Card key={word.id ?? index} className={styles.conjugationCard}>
                        <Card.Header className={styles.conjugationHeader}>
                            {renderText(word.infinitive, { as: 'span' })}
                            <span>→</span>
                            {renderText(word.conjugatedForm, { as: 'span', className: 'text-primary' })}
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-1">Example:</div>
                            <div className={styles.conjugationExample}>
                                <div className={styles.sentenceContent}>
                                    {renderText(word.exampleSentence, { as: 'p', className: styles.nativeSentenceText })}
                                    <p className={styles.translationText}><em>{word.sentenceTranslation}</em></p>
                                </div>
                                <div className={styles.sentenceActions}>
                                    <VolumeUpFill className={styles.speakIcon} onClick={() => handleSpeak(word.exampleSentence)} />
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                ))
            }
        </div>
    );
};

export default ConjugationLesson;