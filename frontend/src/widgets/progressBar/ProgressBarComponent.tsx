import React, { useState, useEffect } from "react";
import { ProgressBar } from "react-bootstrap";
import styles from './progress-bar.module.scss';

interface ProgressBarComponentProps {
    isLoading: boolean;
    progress: number;
}

const ProgressBarComponent:React.FC<ProgressBarComponentProps> = ({isLoading, progress}) => {
    const [show, setShow] = useState(false);
    const [animatedProgress, setAnimatedProgress] = useState(0);

    useEffect(() => {
        let timerId: NodeJS.Timeout;

        if(isLoading) {
            setShow(true);
        }
        else if(show) {
            // Enter this condition when isLoading becomes false
            timerId = setTimeout(() => {
                setShow(false);
            }, 500);
        }

        return () => clearTimeout(timerId);
    }, [isLoading, show]);

    // Effect to smoothly animate the progress value towards the target 'progress' property
    useEffect(() => {
        if (show) {
            const interval = setInterval(() => {
                setAnimatedProgress(prev => {
                    if (prev < progress) {
                        return Math.min(prev + 1, progress);
                    }
                    clearInterval(interval);
                    return prev;
                })
            }, 30);
            return () => clearInterval(interval);
        }
        else {
            setAnimatedProgress(0);
        }
    }, [progress, show]);

    if (!show) return null;

    return(
        <ProgressBar
            className={styles.progressBar}
            now={animatedProgress}
            animated
            label={`${Math.round(animatedProgress)}%`}
        />
    )
}
export default ProgressBarComponent;