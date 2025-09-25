import React from 'react';
import styles from '../../../../shared/ui/book/bookpage.module.scss';

interface BehindCoverPageProps {}
// This component is designed to be the page right behind the front cover of the book.
// Forward Ref is necessary for the page flipping animation.
const BehindCoverPage = React.forwardRef<HTMLDivElement, BehindCoverPageProps>((_props, ref) => {
    return (
        <div ref={ref}>
            <div className={styles['behind-cover-wrapper']}>
                <div className={`${styles['behind-cover-paper']} ms-3`}>
                    <div className={styles['page-content']}>
                        <h1>Welcome to your Storybook</h1>
                        <p>This is an interactive book where you can generate short stories on any topic.</p>
                        <h2>How to start:</h2>
                        <ul>
                            <li>Use the settings gear to pick your language and level.</li>
                            <li>Select a genre, and either suggest a topic or let us generate a random one for you!</li>
                            <li>Press "Generate" to create your custom short story!</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    )
});

export default BehindCoverPage;