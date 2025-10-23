import React from "react";
import {StoryBookLibraryItemDTO} from "../../../shared/types/dto";
import styles from './storybookitem.module.scss';
import { getCountryCodeForLanguage } from "../../../shared/utils/languageUtils";
import { FlagIcon as Flag } from "react-flag-kit";
import { getStoryBookDifficultyColor } from "../../../shared/utils/colorUtils";
import { getStoryBookDesign } from "../../../shared/utils/designUtils";

interface StoryBookItemProps {
    book: StoryBookLibraryItemDTO;
    onClick: () => void;
}

const StoryBookItem: React.FC<StoryBookItemProps> = ({ book, onClick }) => {
    const bookStyle = {
        // Use a CSS variable to set the base color for the gradient in the stylesheet
        '--storybook-color': getStoryBookDifficultyColor(book.difficulty)
    } as React.CSSProperties;

    const countryCode = getCountryCodeForLanguage(book.language);
    const designClass = styles[getStoryBookDesign(book.id)] || '';

    return(
        <div className={`${styles.book} ${designClass}`} style={bookStyle} onClick={onClick}>
            {countryCode && <Flag code={countryCode} size={32} className={styles.flag} />}
            <h4 className={styles.title}>{book.title}</h4>

            <div className={styles.info}>
                <span>{book.language}</span>
            </div>

            <div className={styles.info}>
                <span>Difficulty: {book.difficulty}</span>
            </div>

            <div className={styles.stats}>
                <span>{book.storyCount} {book.storyCount > 1 ? 'Stories' : 'Story'}</span>
                <span>{book.pageCount} {book.pageCount > 1 ? 'Pages' : 'Page'}</span>
            </div>
        </div>
    )
};
export default StoryBookItem;