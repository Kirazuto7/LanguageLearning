import React from 'react';
import styles from './bookpage.module.scss';

interface ChapterPageProps {
  pageNumber: number;
  children: React.ReactNode;
  isRightPage: boolean;
  chapterNumber: number;
  chapterNativeTitle: string;
  chapterTitle: string;
}
/**
 * Renders the content for the first lessonPage of a lessonChapter.
 */
const ChapterPage = React.forwardRef<HTMLDivElement, ChapterPageProps>(({ pageNumber, children, isRightPage = true, chapterNumber, chapterNativeTitle, chapterTitle }, ref) => {
const pageSideClass = isRightPage ? styles['right-active-page'] : styles['left-active-page'];

  // The outer div is for the library. It gets the ref.
  // The inner div is for component styling.
  return (
    <div className='h-100' ref={ref}>
      <div className={`${styles['active-page']} ${pageSideClass}`}>
        <div className={styles['page-content']}>
            <h3 className="text-center mb-2">Chapter {chapterNumber}</h3>
            <h2 className="text-center mb-1">{chapterNativeTitle}</h2>
            <h6 className="text-center mb-4">{chapterTitle}</h6>
            {children}
        </div>
        {pageNumber && <div className={`${styles['page-number']} small`}>{pageNumber}</div>}
      </div>
    </div>
  );
});

export default ChapterPage;
