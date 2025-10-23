import React from "react";
import styles from './fuji-background.module.scss';

const FujiBackground:React.FC = () => {

    const petals = Array.from({ length: 30 });

    return(
        <div className={styles.fujiContainer}>
            <div className={styles.cloud1} />
            <div className={styles.cloud2} />
            <div className={styles.cloud3} />
            <div className={styles.fujiMountain}/>
            <div className={styles.cherryBranch1}/>
            <div className={styles.cherryBranch2}/>
            <div className={styles.petalContainer}>
                {petals.map((_, index) => (
                    <div className={styles.petal} key={index}/>
                ))}
            </div>
        </div>
    )
}

export default FujiBackground;