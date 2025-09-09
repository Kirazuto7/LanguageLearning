import React from "react";
import styles from './sunset-background.module.scss';

const SunsetBackground: React.FC = () => {
    return (
        <div className={styles.sunContainer}>
            <div className={styles.sun} />
            <div className={styles.sea} />
        </div>
    );
};

export default SunsetBackground;