import React from 'react';
import styles from '../styles/bookpage.module.css';

const BookPage = React.forwardRef(({ pageNumber, children, isRightPage = true }, ref) => {
  const pageSideClass = isRightPage ? styles['right-active-page'] : styles['left-active-page'];

  // The outer div is for the library. It gets the ref.
  // The inner div is for our styles.
  return (
    <div ref={ref}>
      <div className={`${styles['active-page']} ${pageSideClass}`}>
        <div className={styles['page-content']}>
          {children}
        </div>
        {pageNumber && <div className={styles['page-number']}>{pageNumber}</div>}
      </div>
    </div>
  );
});

export default BookPage;