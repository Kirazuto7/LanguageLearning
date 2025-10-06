import React from 'react';
import styles from './lessonbookillustration.module.scss';

const LessonBookIllustration: React.FC = () => {
    return (
        <div className={styles.bookContainer}>
            <div className={`${styles.book} ${styles.book1}`}> {/* Bottom book */}
                <div className={styles.bookSpine}></div>
                <div className={styles.bookBottom}></div>
            </div>
            <div className={`${styles.book} ${styles.book2}`}> {/* Middle book */}
                <div className={styles.bookSpine}></div>
                <div className={styles.bookBottom}></div>
            </div>
            <div className={`${styles.book} ${styles.book3}`}> {/* Top book */}
                <div className={styles.bookSpine}></div>
                <div className={styles.bookBottom}></div>
            </div>
            <div className={styles.cap}>
                <div className={styles.capBase} />
                <div className={styles.capTop}>
                    <div className={styles.tassel}>
                        <div className={styles.tasselString}></div>
                        <div className={styles.tasselEnd}></div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LessonBookIllustration;