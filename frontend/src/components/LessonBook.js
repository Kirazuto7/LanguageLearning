import React, {useState, useEffect, useCallback, useRef} from 'react';
import styles from '../styles/lessonbook.module.css';
import { useLanguage } from '../contexts/LanguageContext';

function LessonBook({ 
  title = "My HTML Content Book",
  initialContent = {},
  onPageChange = () => {},
  onContentChange = () => {}
}) {
  const { languageName } = useLanguage();
  const [currentPage, setCurrentPage] = useState(1);
  
  const hasTurnedPage = useRef(false);
  const totalPages = 4;

  const [pageContents, setPageContents] = useState({
    1: `<h1>Welcome to your Language Learning Book</h1>
        <p>This is an interactive book template where you can store and display HTML content. Use the editor panel to add your own content to each page.</p>
        <h2>Features:</h2>
        <ul>
            <li>Multiple pages with navigation</li>
            <li>HTML content editor</li>
            <li>Realistic book appearance</li>
            <li>Responsive design</li>
        </ul>
        <p>Click the "Edit Content" button to start adding your own HTML templates and information!</p>`,
    2: `<h1>Getting Started</h1>
        <h2>How to Use:</h2>
        <ol>
            <li>Click "Edit Content" to open the editor</li>
            <li>Select which page to edit</li>
            <li>Enter your HTML content</li>
            <li>Click "Update Page" to see changes</li>
        </ol>
        <h2>HTML Examples:</h2>
        <p>You can use any HTML tags like:</p>
        <ul>
            <li><code>&lt;h1&gt;</code> for headings</li>
            <li><code>&lt;p&gt;</code> for paragraphs</li>
            <li><code>&lt;ul&gt;</code> for lists</li>
            <li><code>&lt;img&gt;</code> for images</li>
            <li><code>&lt;div&gt;</code> for containers</li>
        </ul>`,
    3: `<h1>Your Content Here</h1>
        <p>This is page 3. Add your own HTML content using the editor!</p>
        <h2>Sample Template:</h2>
        <div style="border: 1px solid #ccc; padding: 15px; margin: 10px 0; border-radius: 5px;">
            <h3>Template Section</h3>
            <p>You can create structured content like this template section.</p>
        </div>`,
    4: `<h1>More Content</h1>
        <p>This is page 4. You can add as much content as you need!</p>
        <h2>Ideas for Content:</h2>
        <ul>
            <li>Documentation templates</li>
            <li>Code snippets</li>
            <li>Reference materials</li>
            <li>Project notes</li>
            <li>Learning resources</li>
        </ul>
        <p><strong>Pro tip:</strong> You can style your content with inline CSS or add classes for custom styling.</p>`,
    ...initialContent
  });

  useEffect(() => {
    onPageChange(currentPage);
  }, [currentPage, onPageChange]);

  useEffect(() => {
    onContentChange(pageContents);
  }, [pageContents, onContentChange]);

  const nextPage = useCallback(() => {
    setCurrentPage(prev => (prev < totalPages - 1 ? prev + 2 : prev));
    }, [totalPages]);

    const previousPage = useCallback(() => {
    setCurrentPage(prev => (prev > 1 ? prev - 2 : prev));
    }, []);

  const setPageContent = (pageNumber, htmlContent) => {
    if (pageNumber >= 1 && pageNumber <= totalPages) {
      setPageContents(prev => ({
        ...prev,
        [pageNumber]: htmlContent
      }));
    }
  };

  const getPageContent = (pageNumber) => {
    return pageContents[pageNumber] || '';
  };

  const goToPage = (pageNumber) => {
    if (pageNumber >= 1 && pageNumber <= totalPages - 1) {
      const newPage = pageNumber % 2 === 1 ? pageNumber : pageNumber - 1;
      setCurrentPage(newPage);
    }
  };

  const getAllContent = () => {
    return { ...pageContents };
  };

  const setAllContent = (contentObject) => {
    setPageContents(prev => ({ ...prev, ...contentObject }));
  };

  // Expose API methods via ref or global object
  useEffect(() => {
    window.bookAPI = {
      setPageContent,
      getPageContent,
      getCurrentPage: () => currentPage,
      goToPage,
      getAllContent,
      setAllContent
    };
  }, [currentPage, pageContents]);

  const leftPage = currentPage;
  const rightPage = currentPage + 1;

  return (
    <div className={styles['book-container']}>
    <div className={styles.book}>
      {/* Left pages stack */}
      <div className={styles['left-pages-stack']}>
        {[...Array(5)].map((_, i) => (
          <div key={i} className={styles['left-page-layer']}></div>
        ))}
      </div>
      
      {/* Right pages stack */}
      <div className={styles['right-pages-stack']}>
        {[...Array(5)].map((_, i) => (
          <div key={i} className={styles['right-page-layer']}></div>
        ))}
      </div>
      
      {/* Main active pages */}
      <div className={styles['main-pages']}>
        <div 
          className={`${styles['active-page']} ${styles['left-active-page']}`}

        >
          <div 
            className={styles['page-content']}
            dangerouslySetInnerHTML={{ __html: pageContents[leftPage] || '' }}
          />
          <div className={styles['page-number']}>{leftPage}</div>
        </div>
        
        <div 
          className={`${styles['active-page']} ${styles['right-active-page']}`}
        >
          <div 
            className={styles['page-content']}
            dangerouslySetInnerHTML={{ 
              __html: rightPage <= totalPages 
                ? pageContents[rightPage] || '' 
                : '<div style="text-align: center; color: #999; margin-top: 200px; font-style: italic; font-size: 18px;">~ End of Book ~</div>' 
            }}
          />
          <div className={styles['page-number']}>
            {rightPage <= totalPages ? rightPage : ''}
          </div>
        </div>
      </div>
      
      {/* Center binding */}
      <div className={styles['book-binding']}></div>
    </div>
    
    <div className={styles['page-indicator']}>
      Page {leftPage}-{Math.min(rightPage, totalPages)} of {totalPages}
    </div>
    
    <div className={styles['navigation']}>
      <button 
        className={styles['navBtn']}
        onClick={previousPage}
        disabled={currentPage <= 1}
      >
        ← Previous
      </button>
      <button 
        className={styles['navBtn']}
        onClick={nextPage}
        disabled={currentPage >= totalPages - 1}
      >
        Next →
      </button>
    </div>
  </div>
  );
}

export default LessonBook;
