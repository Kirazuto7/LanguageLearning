import React from 'react';
import styles from '../../bookpages/bookpage.module.css';

interface InstructionsPageProps {
    children: React.ReactNode;
}
const InstructionsPage = React.forwardRef<HTMLDivElement, InstructionsPageProps>(({children}, ref) => {
    return (
        <div ref={ref}>
            <div className={`${styles['active-lessonPage']} ${styles['right-active-lessonPage']}`}>
                <div className={styles['lessonPage-content']}>
                    {children}
                </div>
                <div className={styles['lessonPage-number']}>0</div>
            </div>
        </div>
    ); 
});

export default InstructionsPage;

