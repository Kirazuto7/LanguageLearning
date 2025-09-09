import React from "react";
import styles from "./hanokvillage.module.scss";
import Hanok from "./Hanok";

const HanokVillageBackground: React.FC = () => {
    return(
        <div className={styles.hanokSceneContainer}>
            <div className={styles.sky} />
            <div className={styles.clouds}>
                <div className={`${styles.cloud} ${styles.cloud1}`} />
                <div className={`${styles.cloud} ${styles.cloud2}`} />
                <div className={`${styles.cloud} ${styles.cloud3}`} />
            </div>
            <div className={styles.birds}>
                <div className={`${styles.bird} ${styles.bird1}`} />
                <div className={`${styles.bird} ${styles.bird2}`} />
                <div className={`${styles.bird} ${styles.bird3}`} />
            </div>
            <div className={styles.mountains} />
            <div className={styles.street} />
            <Hanok className={`${styles.distantHanok} ${styles.distantLeft}`} />
            <Hanok className={`${styles.distantHanok} ${styles.distantRight}`} />
            <div className={styles.ginkgoTree}>
                <div className={styles.trunk}>
                    <div className={`${styles.branch} ${styles.branch1}`} />
                    <div className={`${styles.branch} ${styles.branch2}`} />
                    <div className={`${styles.branch} ${styles.branch3}`} />
                </div>
            </div>
            <div className={`${styles.ginkgoTree} ${styles.ginkgoTreeRight}`}>
                <div className={styles.trunk}>
                    <div className={`${styles.branch} ${styles.branch1}`} />
                    <div className={`${styles.branch} ${styles.branch2}`} />
                    <div className={`${styles.branch} ${styles.branch3}`} />
                </div>
            </div>
            <Hanok className={styles.mainHanok} />
        </div>
    );
}

export default HanokVillageBackground;