import React from 'react';
import { Button } from 'react-bootstrap';
import styles from './actioncard.module.scss';

interface ActionCardProps {
    title: string;
    description: string;
    buttonText: string;
    onButtonClick: () => void;
    illustration: React.ReactNode;
}

const ActionCard: React.FC<ActionCardProps> = ({ title, description, buttonText, onButtonClick, illustration }) => {
    return (
        <div className={styles.actionCard} onClick={onButtonClick}>
            <div className={styles.illustration}>{illustration}</div>
            <div className={styles.content}>
                <h5>{title}</h5>
                <p className="text-muted small">{description}</p>
            </div>
            <Button variant="primary" className="mt-auto">{buttonText}</Button>
        </div>
    );
};

export default ActionCard;