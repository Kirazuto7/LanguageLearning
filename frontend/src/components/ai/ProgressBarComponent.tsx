import React, { useState, useEffect } from "react";
import {ProgressBar} from "react-bootstrap";
import styles from './progress-bar.module.scss';

interface ProgressBarComponentProps {
    isLoading: boolean;
    progress: number;
}

const ProgressBarComponent:React.FC<ProgressBarComponentProps> = ({isLoading, progress}) => {
    const [show, setShow] = useState(false);
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

    if (!show) return null;

    return(
        <ProgressBar
            className={styles.progressBar}
            now={progress}
            animated
            label={`${Math.round(progress)}%`}
        />
    )
}
export default ProgressBarComponent;