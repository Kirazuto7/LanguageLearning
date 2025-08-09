/**
 * Renders the table of contents navigation page.
 */        
const TableOfContentsPage = (({ chapters, onNavigate }) => {

  // The outer div is for the library. It gets the ref.
  // The inner div is for our styles.
    return (
        <div>
            <h2>Table of Contents</h2>
            <ul>
                {
                    chapters.map(({chapterNumber, title, startingPageNumber}) => (
                        <li key={chapterNumber} style={{margin: '10px 0'}}>
                            <button
                                onClick={() => onNavigate(startingPageNumber)}
                                style={{
                                cursor: 'pointer',
                                background: 'none',
                                border: 'none',
                                color: 'blue',
                                textDecoration: 'underline',
                                fontSize: '1rem',
                                }}
                            > 
                                {chapterNumber}. {title}
                            </button>
                        </li>
                    ))
                }
            </ul>
        </div>
    );
});

export default TableOfContentsPage;