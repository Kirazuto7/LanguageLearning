import React from 'react';
import styles from '../../bookpages/bookpage.module.css';

interface BehindCoverPageProps {}
// This component is designed to be the lessonPage right behind the front cover of the book.
// Forward Ref is necessary for the lessonPage flipping animation.
const BehindCoverPage = React.forwardRef<HTMLDivElement, BehindCoverPageProps>((_props, ref) => {
    return (
        <div ref={ref}>
            <div className="d-flex justify-content-center align-items-center" style={{backgroundColor: '#333', width: '100%', height: '100%', boxShadow: '0 0 20px #ff69b4'}}>
                <div className="ms-3" style={{backgroundColor: 'white', width: '98%', height: '95%'}}>
                    <div className={styles['lessonPage-content']}>
                        <h1>Welcome to your Language Learning Book</h1>
                        <p>This is an interactive book where you can generate lessons on any topic.</p>
                        <h2>How to start:</h2>
                        <ul>
                            <li>Use the settings gear to pick your language and level.</li>
                            <li>Suggest a topic in the input box below.</li>
                            <li>Press "Send" to generate your custom lesson!</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    )
});

export default BehindCoverPage;