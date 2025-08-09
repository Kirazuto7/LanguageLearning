import React from 'react';
import styles from '../../styles/bookpage.module.css';

const InstructionsPage = React.forwardRef(({children}, ref) => {
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

