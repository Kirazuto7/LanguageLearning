import React from "react";
import styles from './cafe-background.module.scss';

const CafeBackground: React.FC = () => {
    return (
        <div className={styles.cafeContainer}>
            <div className={styles.window}/>
            <div className={styles.vine1}/>
            <div className={styles.vine2}/>
            <div className={styles.vine3}/>
            <div className={styles.floor}/>
            <div className={styles.pottedPlant2}/>
            <div className={styles.cafeTable}/>
            <div className={styles.cafeChair1} />
            <div className={styles.cafeChair2} />
            <div className={styles.icedAmericano1} />
            <div className={styles.icedAmericano2} />
            <div className={styles.tablePlant} />
            <div className={styles.shelf} />
            <div className={styles.shelfPlant1} />
            <div className={styles.shelfPlant2} />
            <div className={styles.shelfVine} />
            <div className={styles.hangingPlant1}>
                <div className={`${styles.hangingVine} ${styles.hangingVine1}`} />
                <div className={`${styles.hangingVine} ${styles.hangingVine2}`} />
            </div>
            <div className={styles.hangingPlant2}>
                <div className={`${styles.hangingVine} ${styles.hangingVine1}`} />
                <div className={`${styles.hangingVine} ${styles.hangingVine2}`} />
            </div>
            <div className={styles.hangingLight1}>
                <div className={styles.bulb} />
            </div>
            <div className={styles.hangingLight2}>
                <div className={styles.bulb} />
            </div>
        </div>
    );
}

export default CafeBackground;