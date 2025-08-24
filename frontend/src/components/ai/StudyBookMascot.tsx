import React, {useState, useEffect} from 'react';
import styles from "./mascot.module.scss";
import { useSettingsManager } from '../../hooks/useSettingsManager';
import Blackboard from "./Blackboard";
import MascotCharacter from "./MascotCharacter";
import StudyBookInputField from "./StudyBookInputField";
import ProgressBarComponent from "./ProgressBarComponent";

interface StudyBookMascotProps {
    onTopicSubmit: (topic: string) => void;
    isLoading: boolean;
    progress: number;
    message: string;
}

const StudyBookMascot: React.FC<StudyBookMascotProps> = ({ onTopicSubmit, isLoading, progress, message }) => {
    const { settings } = useSettingsManager();
    const [hop, setHop] = useState<boolean>(false);
    const [speech, setSpeech] = useState<string>(`Ready to learn? Suggest a topic to get started! 😄`);

    useEffect(() => {
        if (isLoading) {
            setSpeech(message);
        }
        else {
            // When the language is changed in settings, update the mascot's speech.
            setSpeech(`Ready to learn some ${settings?.language || 'new things'}? Suggest a topic to get started! (^_^)`);
        }
    }, [isLoading, message, settings]);

    const handleSendButton = (topic: string) => {
        setHop(true);
        setTimeout(() => {
            setHop(false);
        }, 600);

        onTopicSubmit(topic);
        // setSpeech(`Great! I'll generate a lesson about "${topic}" for you!`);
    }

    return(
        <div id={styles.mascotContainer}>
            <div className={styles.mascotContentWrapper}>
                <div className={styles.blackboardWrapper}>
                    <Blackboard text={speech}/>
                </div>
                <div className={styles.characterWrapper}>
                    <MascotCharacter hop={hop}/>
                </div>
                <div className={`${styles.inputRow} mt-3`}>
                    <StudyBookInputField onSend={handleSendButton} disabled={isLoading}/>
                </div>
                <div className={styles.progressWrapper}>
                    <ProgressBarComponent isLoading={isLoading} progress={progress}/>
                </div>
            </div>
        </div>
    )
}

export default StudyBookMascot;
