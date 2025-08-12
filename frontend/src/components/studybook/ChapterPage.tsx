import React from 'react';
import styles from '../../styles/bookpage.module.css';

interface ChapterPageProps {
  pageNumber: number;
  children: React.ReactNode;
  isRightPage: boolean;
  chapterNumber: number;
  chapterNativeTitle: string;
  chapterTitle: string;
}
/**
 * Renders the content for the first page of a chapter.
 */        
const ChapterPage = React.forwardRef<HTMLDivElement, ChapterPageProps>(({ pageNumber, children, isRightPage = true, chapterNumber, chapterNativeTitle, chapterTitle }, ref) => {
const pageSideClass = isRightPage ? styles['right-active-page'] : styles['left-active-page'];

  // The outer div is for the library. It gets the ref.
  // The inner div is for component styling.
  return (
    <div ref={ref}>
      <div className={`${styles['active-page']} ${pageSideClass}`}>
        <div className={styles['page-content']}>
            <h5 className="text-center mb-2">Chapter {chapterNumber}</h5>
            <h4 className="text-center mb-1">{chapterNativeTitle}</h4>
            <p className="text-center mb-4">{chapterTitle}</p>
            {children}
        </div>
        {pageNumber && <div className={styles['page-number']}>{pageNumber}</div>}
      </div>
    </div>
  );
});

export default ChapterPage;


