import styles from '../../styles/bookpage.module.css';
/**
 * Renders the table of contents navigation page.
 */        
const TableOfContentsPage = (({ chapters = [], onNavigate }) => {

  // The outer div is for the library. It gets the ref.
  // The inner div is for our styles.
    return (
        <div>
            <h1>Table of Contents</h1>
            <ul>
                {
                    chapters.map(({chapterNumber, title, startingPageNumber}) => (
                        <li key={chapterNumber} style={{margin: '10px 0', listStyle: 'none'}}>
                            <button
                                onClick={() => onNavigate(startingPageNumber)}
                                style={{
                                cursor: 'pointer',
                                background: 'none',
                                border: 'none',
                                color: 'blue',
                                fontSize: '1rem',
                                width: '100%'
                                }}
                            > 
                                <div className="d-flex flex-column align-items-start">
                                    <div className="fw-bold">Chapter {chapterNumber}</div>
                                    <div className="d-flex justify-content-between w-100 ">
                                        <span className={styles['toc-title']}>{title}</span>
                                        <span className={styles['toc-dots']}></span>
                                        <span className={styles['toc-page-number']}>{startingPageNumber}</span>
                                    </div>
                                </div>
                            </button>
                        </li>
                    ))
                }
            </ul>
        </div>
    );
});

export default TableOfContentsPage;