import React from 'react';
import styles from '../../styles/bookpage.module.css';

const InstructionsPage = React.forwardRef((_props, ref) => {
    return (
        <div ref={ref}>
            <div className={`${styles['active-page']} ${styles['right-active-page']}`}>
                <div className={styles['page-content']}>
                    <h1>Welcome to your Language Learning Book</h1>
                    <p>This is an interactive book where you can generate lessons on any topic.</p>
                    <h2>How to start:</h2>
                    <ul>
                        <li>Use the settings gear to pick your language and level.</li>
                        <li>Suggest a topic in the input box below.</li>
                        <li>Press "Send" to generate your custom lesson!</li>
                    </ul>
                    <p>Your new lesson(s) will appear here.</p>
                </div>
                <div className={styles['page-number']}>0</div>
            </div>
        </div>
    ); 
});

export default InstructionsPage;

