import React, {useState, useEffect} from 'react';
import styles from "./mascot.module.scss";
import { useSettingsManager } from '../../hooks/useSettingsManager';
import Blackboard from "./Blackboard";
import MascotCharacter from "./MascotCharacter";
import StudyBookInputField from "./StudyBookInputField";

interface StudyBookMascotProps {
    onTopicSubmit: (topic: string) => void;
    isLoading?: boolean;
}

const StudyBookMascot: React.FC<StudyBookMascotProps> = ({ onTopicSubmit, isLoading = false }) => {
    const { settings } = useSettingsManager();
    const [hop, setHop] = useState<boolean>(false);
    const [speech, setSpeech] = useState<string>(`Let's learn ${settings?.language || ' a new language'}! What should our first topic be? 😄`);

    useEffect(() => {
        // When the language is changed in settings, update the mascot's speech.
        setSpeech(`Let's learn ${settings?.language || ' a new language'}! What should our next topic be? (^_^)`);
    }, [settings]);

    const handleSendButton = (topic: string) => {
        setHop(true);
        setTimeout(() => {
            setHop(false);
        }, 600);

        if (onTopicSubmit) {
            onTopicSubmit(topic);
            setSpeech(`Great! I'll generate a lesson about "${topic}" for you!`);
        }
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
            </div>
        </div>
    )
}

export default StudyBookMascot;
