import React from 'react';
import { WordDTO } from '../../../../shared/types/dto';
import { useWordDisplay } from '../../hooks/useWordDisplay';
import styles from "../lesson.module.scss";
import { VolumeUpFill } from "react-bootstrap-icons";

interface WordCardProps {
    word: WordDTO;
    index: number;
    onSpeak: (text: string) => void;
}

/**
 * Renders a single vocabulary word card with its details and a speak button.
 */
const WordCard: React.FC<WordCardProps> = ({ word, index, onSpeak }) => {
    const { nativeWordDisplay, textToSpeak, detailsDisplay } = useWordDisplay(word);

    return (
        <div className={styles.vocabCard}>
            <div className={styles.sentenceNumber}>
                <div className={styles.circularNumber}>{index + 1}</div>
            </div>
            <div className={styles.vocabContent}>
                <h4 className={styles.vocabWord}>{nativeWordDisplay}</h4>
                <p className={styles.vocabTranslation}>{word.englishTranslation}</p>
                {detailsDisplay && <div className={styles.vocabDetails}>{detailsDisplay}</div>}
            </div>
            <div className={styles.sentenceActions}>
                <VolumeUpFill
                    className={styles.speakIcon}
                    onClick={() => onSpeak(textToSpeak)}
                />
            </div>
        </div>
    );
};

export default WordCard;