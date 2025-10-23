import React from 'react';
import styles from './starry-background.module.scss';

const StarryBackground: React.FC = () => {
    return (
        <>
            <div className={styles.stars1} />
            <div className={styles.stars2} />
            <div className={styles.stars3} />
        </>
    );
};

export default StarryBackground;