import React, {Suspense, useState, useEffect} from 'react';
import styles from "../../shared/components/mascot/mascot.module.scss";
import lessonMascotStyles from "./lessonBookMascot.module.scss";
import { useSettingsManager } from '../../features/userSettings/hooks/useSettingsManager';
import Blackboard from "../../shared/components/blackboard/Blackboard";
import ChapterGenerationInputField from "../../features/lessonBook/chapterGeneration/components/ChapterGenerationInputField";
import ProgressBarComponent from "../progressBar/ProgressBarComponent";
import Mascot from "../../shared/components/mascot/Mascot";
import {mascotGenders} from "../../shared/types/types";

interface LessonBookMascotProps {
    onTopicSubmit: (topic: string) => void;
    isLoading: boolean;
    progress: number;
    message: string;
    celebrationTrigger?: number;
}

const LessonBookMascot: React.FC<LessonBookMascotProps> = ({ onTopicSubmit, isLoading, progress, message,celebrationTrigger }) => {
    const { settings } = useSettingsManager();
    const [hop, setHop] = useState<boolean>(false);
    const [celebrate, setCelebrate] = useState<boolean>(false);
    const [speech, setSpeech] = useState<string>(`Ready to learn? Suggest a topic to get started! 😄`);

    useEffect(() => {
        if (celebrate) return;

        if (isLoading && message) {
            setSpeech(message);
        }
        else if (!isLoading) {
            // When the language is changed in settings, update the mascot's speech.
            setSpeech(`Ready to learn some ${settings?.language || 'new things'}? Suggest a topic to get started! 😄`);
        }
    }, [isLoading, message, settings?.language, celebrate]);

    useEffect(() => {
        if (celebrationTrigger && celebrationTrigger > 0) {
            setSpeech("You got them all right! Great job! 🎉");
            setCelebrate(true);

            const timer = setTimeout(() => {
                setCelebrate(false);
            }, 2500)

            return () => clearTimeout(timer);
        }
    }, [celebrationTrigger]);

    const handleSendButton = (topic: string) => {
        setHop(true);
        setTimeout(() => {
            setHop(false);
        }, 600);

        onTopicSubmit(topic);
        // setSpeech(`Great! I'll generate a lesson about "${topic}" for you!`);
    }

    return(
        <div id={styles.mascotContainer} className={lessonMascotStyles.lessonMascotContainer}>
            <div id={styles.mascotContentWrapper}>
                <div className={styles.visualsWrapper}>
                    <div className={styles.blackboardWrapper}>
                        <Blackboard
                            text={speech}
                            gender={mascotGenders[settings?.mascot || 'jinny']}
                            forceSpeak={isLoading || celebrate}
                        />
                    </div>
                    <div className={styles.characterWrapper}>
                        <Suspense fallback={<div style={{ height: '120px', width: '120px'}}/>}>
                            <Mascot character={settings?.mascot || 'jinny'} hop={hop} celebrate={celebrate}/>
                        </Suspense>
                    </div>
                </div>
                <div className={`${styles.inputRow} mt-3`}>
                    <ChapterGenerationInputField onSend={handleSendButton} disabled={isLoading}/>
                </div>
                <div className={styles.progressWrapper}>
                    <ProgressBarComponent isLoading={isLoading} progress={progress}/>
                </div>
            </div>
        </div>
    )
}

export default LessonBookMascot;
