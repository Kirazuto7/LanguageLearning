import React from "react";
import {LessonBookLibraryItemDTO} from "../../../shared/types/dto";
import styles from './lessonbookitem.module.scss';
import {getDifficultyColor} from "../../../shared/utils/colorUtils";
import {getCountryCodeForLanguage} from "../../../shared/utils/languageUtils";
import { FlagIcon as Flag } from 'react-flag-kit';

interface LessonBookItemProps {
    book: LessonBookLibraryItemDTO;
    onClick: () => void;
}

const LessonBookItem: React.FC<LessonBookItemProps> = ({ book, onClick }) => {
    const bookStyle = {
        '--book-cover-color': getDifficultyColor(book.difficulty)
    } as React.CSSProperties;

    const countryCode = getCountryCodeForLanguage(book.language);

    return(
        <div className={styles.book} style={bookStyle} onClick={onClick}>
            {countryCode && <Flag code={countryCode} size={32} className={styles.flag} />}
            <h4 className={styles.title}>{book.title}</h4>

            <div className={styles.info}>
                <span>{book.language}</span>
            </div>

            <div className={styles.info}>
                <span>Difficulty: {book.difficulty}</span>
            </div>

            <div className={styles.stats}>
                <span>{book.chapterCount} {book.chapterCount > 1 ? 'Chapters' : 'Chapter'}</span>
                <span>{book.pageCount} {book.pageCount > 1 ? 'Pages' : 'Page'}</span>
            </div>
        </div>
    )
};
export default LessonBookItem;