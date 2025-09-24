import React from 'react';
import { GrammarLessonDTO, LessonSentenceDTO } from '../../../shared/types/dto';
import parse from 'html-react-parser';
import { useSettingsManager } from '../../userSettings/hooks/useSettingsManager';
import useTextToSpeech from "../../../hooks/useTextToSpeech";
import { mascotGenders, MascotName } from "../../../shared/types/types";
import styles from "./lesson.module.scss";
import { VolumeUpFill } from "react-bootstrap-icons";
import { getPhoneticText } from "../../../shared/utils/textUtils";

interface GrammarLessonProps {
    lesson: GrammarLessonDTO;
}

/**
 * Renders the content for a grammar lesson, displaying the grammar concept and example sentences.
*/
const GrammarLesson: React.FC<GrammarLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();
    const isJapanese = settings?.language.toLowerCase() === 'japanese';

    const handleSpeak = (text: string) => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            cancel();
            const textToSpeech = isJapanese ? getPhoneticText(text) : text;
            speak(textToSpeech, settings.language, gender);
        }
    };

    const renderText = (text: string, { as: Component = 'div' as React.ElementType, className = '' } = {}) => {
        return <Component className={className}>{parse(text)}</Component>;
    };

    return (
        <div>
            <h2 className="text-center mb-4">{lesson.title}</h2>
            
            <h6>{lesson.grammarConcept}</h6>
            <h6>{lesson.explanation}</h6>
            <hr />
            <h6 className="mt-3">Examples:</h6>
            {
                lesson.exampleLessonSentences.map((sentence: LessonSentenceDTO, index) => (
                    <div key={sentence.id ?? index} className={styles.exampleSentence}>
                        <div className={styles.sentenceNumber}>
                            <div className={styles.circularNumber}>{index + 1}</div>
                        </div>
                        <div className={styles.sentenceContent}>
                            {renderText(sentence.text, { as: 'p', className: styles.nativeSentenceText })}
                            <p className={styles.translationText}>{sentence.translation}</p>
                        </div>
                        <div className={styles.sentenceActions}>
                            <VolumeUpFill className={styles.speakIcon} onClick={() => handleSpeak(sentence.text)}/>
                        </div>
                    </div>
                ))
            }
        </div>
    );
};

export default GrammarLesson;