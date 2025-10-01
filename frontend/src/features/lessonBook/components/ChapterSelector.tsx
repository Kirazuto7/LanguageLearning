import React from 'react';
import { Form } from 'react-bootstrap';
import { ChapterDTO } from '../../../shared/types/dto';
import styles from './lessonBookViewer.module.scss';

interface ChapterSelectorProps {
    chapters: ChapterDTO[];
    activeChapterIndex: number;
    onChapterSelect: (index: number) => void;
}

export const ChapterSelector: React.FC<ChapterSelectorProps> = ({ chapters, activeChapterIndex, onChapterSelect }) => {
    if (!chapters || chapters.length === 0) {
        return null;
    }

    const handleSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
        onChapterSelect(Number(e.target.value));
    };

    return (
        <div className="d-flex justify-content-center mb-4">
            <Form.Select aria-label="Chapter select" value={activeChapterIndex} onChange={handleSelect} className={styles['lessonChapter-select']}>
                {chapters.map((lessonChapter, index) => (
                    <option key={index} value={index}>
                        Chapter {index + 1}: {lessonChapter.title}
                    </option>
                ))}
            </Form.Select>
        </div>
    );
};