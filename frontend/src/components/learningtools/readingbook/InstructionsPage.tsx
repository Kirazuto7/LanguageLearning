import React from 'react';
import styles from '../../../styles/bookpage.module.css';

interface InstructionsPageProps {
    children: React.ReactNode;
}
const InstructionsPage = React.forwardRef<HTMLDivElement, InstructionsPageProps>(({children}, ref) => {
    return (
        <div ref={ref}>
            <div className={`${styles['active-page']} ${styles['right-active-page']}`}>
                <div className={styles['page-content']}>
                    {children}
                </div>
                <div className={styles['page-number']}>0</div>
            </div>
        </div>
    ); 
});

export default InstructionsPage;

