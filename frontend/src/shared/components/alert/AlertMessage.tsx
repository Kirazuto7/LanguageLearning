import React, { useState, useEffect } from 'react';
import { Alert } from 'react-bootstrap';
import styles from './alertmessage.module.scss';
import { AlertLevel } from "../../types/types";

interface AlertMessageProps {
    message: string;
    level: AlertLevel;
    timeout?: number; // Optional timeout in milliseconds
    onClose?: () => void; // Optional callback when the alert is closed
}

const AlertMessage: React.FC<AlertMessageProps> = ({ message, level, timeout, onClose }) => {
    const [show, setShow] = useState(true);

    // Map our custom levels to react-bootstrap variants
    const getVariant = (level: AlertLevel) => {
        switch (level) {
            case AlertLevel.WARN:
                return 'warning';
            case AlertLevel.ERROR:
                return 'danger';
            case AlertLevel.INFO:
            default:
                return 'info';
        }
    };

    useEffect(() => {
        if (timeout) {
            const timer = setTimeout(() => {
                setShow(false);
                if (onClose) {
                    onClose();
                }
            }, timeout);

            return () => clearTimeout(timer); // Cleanup timer on component unmount
        }
    }, [timeout, onClose]);

    if (!show) {
        return null;
    }

    return (
        <Alert
            variant={getVariant(level)}
            onClose={() => {
                setShow(false);
                if (onClose) {
                    onClose();
                }
            }}
            dismissible
            className={styles.alertContainer}
        >
            {message}
        </Alert>
    );
};

export default AlertMessage;