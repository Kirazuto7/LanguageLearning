import React, { useEffect } from 'react';
import {useTheme} from "../contexts/ThemeContext";
import styles from './layout.module.scss';
import Backgrounds from "../components/backgrounds/Backgrounds";
import {Outlet} from "react-router-dom";

const BackgroundLayout: React.FC = () => {
    const { theme } = useTheme();

    useEffect(() => {
        document.body.classList.add('themed-body', theme);

        return () => {
            document.body.classList.remove('themed-body', theme);
        }
    }, [theme]);

    return (
        <div className={`${styles.layoutContainer}`}>
            <Backgrounds />
            <main className={styles.content}>
                <Outlet />
            </main>
        </div>
    )
}

export default BackgroundLayout;