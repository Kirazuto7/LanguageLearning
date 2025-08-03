import React from 'react';
import styles from '../styles/bookpage.module.css';

const BookPage = React.forwardRef(({ pageNumber, children, isRightPage = true }, ref) => {
  const pageSideClass = isRightPage ? styles['right-active-page'] : styles['left-active-page'];

  return (
    <div className={`${styles['active-page']} ${pageSideClass}`} ref={ref}>
      <div className={styles['page-content']}>
        {children}
      </div>
      {pageNumber && <div className={styles['page-number']}>{pageNumber}</div>}
    </div>
  );
});

export default BookPage;