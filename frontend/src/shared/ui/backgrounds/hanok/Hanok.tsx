import React from "react";
import styles from "./hanok.module.scss";

const HanokWindow: React.FC = () => (
    <div className={styles.windowContainer}>
        {/* The four vertical window panes */}
        <div className={styles.windowPane} />
        <div className={styles.windowPane}>
            <div className={styles.handleRight} />
        </div>
        <div className={styles.windowPane}>
            <div className={styles.handleLeft} />
        </div>
        <div className={styles.windowPane} />
    </div>
);

const SukiwaTile: React.FC = () => <div className={styles.sukiwaTile} />;

const AmkiwaTile: React.FC = () => <div className={styles.amkiwaTile} />;

const MidRoof: React.FC = () => {
    const tilePairs = 26; // Number of sukiwa/amkiwa pairs
    return (
        <div className={styles.midRoof}>
            <div className={styles.roofTileLayer}>
                {/* Create an alternating pattern of Sukiwa and Amkiwa tiles */}
                {Array.from({ length: tilePairs }).flatMap((_, i) => [
                    <SukiwaTile key={`sukiwa-${i}`} />,
                    <AmkiwaTile key={`amkiwa-${i}`} />
                ])}
                {/* Add one last Sukiwa to finish the pattern */}
                <SukiwaTile key="sukiwa-last" />
            </div>
        </div>
    );
};

const MainRoof: React.FC = () => {
    const mainTilePairs = 30;
    return (
        <div className={styles.mainRoof}>
            <div className={styles.roofTileLayer}>
                {Array.from({ length: mainTilePairs }).flatMap((_, i) => [
                    <SukiwaTile key={`sukiwa-main-${i}`} />,
                    <AmkiwaTile key={`amkiwa-main-${i}`} />
                ])}
                <SukiwaTile key="sukiwa-main-last" />
            </div>
            <div className={styles.ridgeBeam} />
        </div>
    );
};

const Hanok: React.FC<{ className?: string }> = ({ className }) => (
    <div className={`${styles.hanokBuilding} ${className || ''}`}>
        <div className={styles.groundFloor}>
            <div className={`${styles.entranceRoom} ${styles.leftRoom}`}/>
            <div className={`${styles.entranceRoom} ${styles.rightRoom}`}/>

            {/* Left Entrance */}
            <div className={`${styles.entranceDoor} ${styles.leftOuter}`} />
            <div className={`${styles.entranceDoor} ${styles.leftInner}`} />
            {/* Right Entrance */}
            <div className={`${styles.entranceDoor} ${styles.rightInner}`} />
            <div className={`${styles.entranceDoor} ${styles.rightOuter}`} />
        </div>
        <div className={`${styles.staircase} ${styles.leftStaircase}`}>
            <div className={styles.step} />
            <div className={styles.step} />
            <div className={styles.step} />
        </div>
        <div className={`${styles.staircase} ${styles.rightStaircase}`}>
            <div className={styles.step} />
            <div className={styles.step} />
            <div className={styles.step} />
        </div>
        <div className={styles.stoneBase} />
        <MidRoof />
        <div className={styles.upperFloor}>
            <HanokWindow />
            <HanokWindow />
            <HanokWindow />
        </div>
        <MainRoof />
    </div>
);

export default Hanok;