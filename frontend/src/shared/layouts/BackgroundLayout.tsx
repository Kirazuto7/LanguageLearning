import React from 'react';
import styles from './layout.module.scss';
import Backgrounds from "../ui/backgrounds/Backgrounds";
import {Outlet} from "react-router-dom";

const BackgroundLayout: React.FC = () => {

    return (
        <div className={styles.layoutContainer}>
            <div className={styles.backgroundCanvas}>
                <Backgrounds />
            </div>
            <main className={styles.content}>
                <Outlet />
            </main>
        </div>
    )
}

export default BackgroundLayout;