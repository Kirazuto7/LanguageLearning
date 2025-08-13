import styles from '../../styles/bookpage.module.css';

export interface TocChapter {
    chapterNumber: number;
    title: string;
    displayPageNumber: number;
    navigationPageIndex: number;
}

interface TableOfContentsPageProps {
    chapters: TocChapter[],
    onNavigate: (pageIndex: number) => void
}
/**
 * Renders the table of contents navigation page.
 */
const TableOfContentsPage: React.FC<TableOfContentsPageProps> = (({ chapters = [], onNavigate }) => {
    return (
        <div>
            <h1>Table of Contents</h1>
            <ul>
                {
                    chapters.map(({chapterNumber, title, displayPageNumber, navigationPageIndex}) => (
                        <li key={chapterNumber} style={{margin: '10px 0', listStyle: 'none'}}>
                            <button
                                onClick={() => onNavigate(navigationPageIndex)}
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
                                        <span className={styles['toc-page-number']}>{displayPageNumber}</span>
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