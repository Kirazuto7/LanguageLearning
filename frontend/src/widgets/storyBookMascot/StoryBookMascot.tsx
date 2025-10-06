import React, { Suspense, useState, useEffect } from 'react';
import storyMascotStyles from "./storyBookMascot.module.scss";
import styles from "../../shared/components/mascot/mascot.module.scss";
import { useSettingsManager } from '../../features/userSettings/hooks/useSettingsManager';
import Blackboard from "../../shared/components/blackboard/Blackboard";
import ProgressBarComponent from "../progressBar/ProgressBarComponent";
import Mascot from "../../shared/components/mascot/Mascot";
import { mascotGenders } from "../../shared/types/types";
import ShortStoryGenerationInputField, { StoryGenerationInput } from "../../features/storyBook/shortStoryGeneration/components/ShortStoryGenerationInputField";

interface StoryBookMascotProps {
    onTopicSubmit: (input: StoryGenerationInput) => void;
    isLoading: boolean;
    progress: number;
    message: string;
}

const StoryBookMascot: React.FC<StoryBookMascotProps> = ({ onTopicSubmit, isLoading, progress, message }) => {
    const { settings } = useSettingsManager();
    const [hop, setHop] = useState<boolean>(false);
    const [speech, setSpeech] = useState<string>(`Ready for an adventure? Pick a genre and topic to start a new story! 📖`);

    useEffect(() => {
        if (isLoading && message) {
            setSpeech(message);
        } else if (!isLoading) {
            setSpeech(`Ready for an adventure in ${settings?.language || 'a new world'}? Pick a genre and topic to start a new story! 📖`);
        }
    }, [isLoading, message, settings?.language]);

    const handleSendButton = (input: StoryGenerationInput) => {
        setHop(true);
        setTimeout(() => {
            setHop(false);
        }, 600);

        onTopicSubmit(input);
    }

    return (
        <div id={styles.mascotContainer} className={storyMascotStyles.storyMascotContainer}>
            <div id={styles.mascotContentWrapper}>
                <div className={styles.visualsWrapper}>
                    <div className={styles.blackboardWrapper}>
                        <Blackboard
                            text={speech}
                            gender={mascotGenders[settings?.mascot || 'jinny']}
                            forceSpeak={isLoading}
                        />
                    </div>
                    <div className={styles.characterWrapper}>
                        <Suspense fallback={<div style={{ height: '120px', width: '120px' }} />}>
                            <Mascot character={settings?.mascot || 'jinny'} hop={hop} />
                        </Suspense>
                    </div>
                </div>
                <div className={`${styles.inputRow} mt-3`}>
                    <ShortStoryGenerationInputField onSend={handleSendButton} disabled={isLoading} />
                </div>
                <div className={styles.progressWrapper}>
                    <ProgressBarComponent isLoading={isLoading} progress={progress} />
                </div>
            </div>
        </div>
    )
}

export default StoryBookMascot;