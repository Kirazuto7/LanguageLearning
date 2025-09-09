import React from 'react';
import styles from './bookpage.module.scss';

interface BookPageProps {
  pageNumber: number,
  children: React.ReactNode,
  isRightPage: boolean
}
const BookPage = React.forwardRef<HTMLDivElement, BookPageProps>(({ pageNumber, children, isRightPage = true }, ref) => {
const pageSideClass = isRightPage ? styles['right-active-page'] : styles['left-active-page'];

  // The outer div is for the library. It gets the ref.
  // The inner div is for our styles.
  return (
    <div className='h-100' ref={ref}>
      <div className={`${styles['active-page']} ${pageSideClass}`}>
        <div className={styles['page-content']}>
          {children}
        </div>
        {pageNumber > 0 && <div className={`${styles['page-number']} small`}>{pageNumber}</div>}
      </div>
    </div>
  );
});

export default BookPage;