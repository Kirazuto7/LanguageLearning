import React from 'react';
import { Pagination } from 'react-bootstrap';
import styles from './lessonBookViewer.module.scss';

interface LessonPaginatorProps {
    pageCount: number;
    activePageIndex: number;
    onPageSelect: (index: number) => void;
}

export const LessonPaginator: React.FC<LessonPaginatorProps> = ({ pageCount, activePageIndex, onPageSelect }) => {
    const paginationItems = Array.from({ length: pageCount }).map((_, i) => {
        const isPageActive = activePageIndex === i;
        const itemClasses = [
            styles['page-item'],
            isPageActive ? styles['active-page-item'] : null,
        ].filter(Boolean).join(' ');

        return (
            <Pagination.Item className={itemClasses} key={i} active={isPageActive} onClick={() => onPageSelect(i)}>
                {i + 1}
            </Pagination.Item>
        );
    });

    return (
        <Pagination className={`${styles['page-control-container']} mb-4`}>
            <Pagination.Prev className={`${styles['page-control-item']} me-4`} onClick={() => onPageSelect(activePageIndex - 1)} disabled={activePageIndex === 0} />
            {paginationItems}
            <Pagination.Next className={`${styles['page-control-item']} ms-4`} onClick={() => onPageSelect(activePageIndex + 1)} disabled={activePageIndex === pageCount - 1} />
        </Pagination>
    );
};