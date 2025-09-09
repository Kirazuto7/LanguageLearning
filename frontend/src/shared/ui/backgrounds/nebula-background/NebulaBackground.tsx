import React from 'react';
import styles from './nebula-background.module.scss';
import StarryBackground from './StarryBackground';

const NebulaBackground: React.FC = () => {
    return (
        <div className={styles.nebulaContainer}>
            <StarryBackground />
            <div className={styles.nebula} />
        </div>
    );
};

export default NebulaBackground;