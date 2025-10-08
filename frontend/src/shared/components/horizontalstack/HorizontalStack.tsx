import React from 'react';
import ScrollContainer from "react-indiana-drag-scroll";
import styles from './horizontalstack.module.scss';

interface HorizontalStackProps {
    title?: string;
    children: React.ReactNode;
    width: string;
    height?: string;
    border?: string;
    borderRadius?: string;
    gap?: string; // e.g., '1rem', '16px'
}

const HorizontalStack: React.FC<HorizontalStackProps> = ({ title, children, width, height, border, borderRadius, gap }) => {
    const containerStyle = {
        width: width,
        height: height,
        border: border,
        borderRadius: borderRadius,
    };

    const stackStyle = {
        gap: gap
    };

    return(
        <ScrollContainer className={styles.container} style={containerStyle}>
            {title && <h3 className={styles.title}>{title}</h3>}
            <div className={styles.stackContainer} style={stackStyle}>
                {children}
            </div>
        </ScrollContainer>
    );
};

export default HorizontalStack;