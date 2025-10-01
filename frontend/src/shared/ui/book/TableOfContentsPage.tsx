import React from 'react';
import styles from './bookpage.module.scss';

export interface TocEntry {
    entryNumber: number;
    title: string;
    navigationPageIndex: number;
}

interface TableOfContentsPageProps {
    entries: TocEntry[];
    onNavigate: (pageIndex: number) => void;
    entryPrefix?: string;
}

const TableOfContentsPage: React.FC<TableOfContentsPageProps> = ({ entries, onNavigate, entryPrefix = 'Entry' }) => {
    return (
        <div className={styles['page-content']}>
            <h1>Table of Contents</h1>
            <p className="lead mt-4">
                Select a story below to begin your reading adventure.
            </p>
            <ul className="list-unstyled mt-4">
                {entries.map((entry) => (
                    <li key={entry.entryNumber} className="mb-3">
                        <a href="#" className={styles['toc-entry']} onClick={(e) => { e.preventDefault(); onNavigate(entry.navigationPageIndex); }}>
                            <span>{entryPrefix} {entry.entryNumber}: {entry.title}</span>
                            <span className={styles['toc-dots']}></span>
                            <span>{entry.navigationPageIndex}</span>
                        </a>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default TableOfContentsPage;