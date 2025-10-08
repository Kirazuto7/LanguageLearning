import React from 'react';
import styles from './storybookillustration.module.scss';

const StoryBookIllustration: React.FC = () => {
    return (
        <>
            <div className={styles.bookContainer}>
                <div className={`${styles.cover} ${styles.leftCover}`}>
                    <div className={`${styles.pageLayer} ${styles.pageLayer1}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer2}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer3}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer4}`} />
                    <div className={styles.leftPage} />
                </div>
                <div className={`${styles.cover} ${styles.rightCover}`}>
                    <div className={`${styles.pageLayer} ${styles.pageLayer1}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer2}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer3}`} />
                    <div className={`${styles.pageLayer} ${styles.pageLayer4}`} />
                    <div className={styles.rightPage} />
                </div>
            </div>
        </>
    );
};

export default StoryBookIllustration;