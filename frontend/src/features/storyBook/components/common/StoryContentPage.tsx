import {StoryContentPageDTO} from "../../../../shared/types/dto";
import React from "react";
import styles from "./storycontentpage.module.scss";


interface StoryContentPageProps {
    page: StoryContentPageDTO;
}

const StoryContentPage: React.FC<StoryContentPageProps> = ({ page }) => {
    const wordsToHighlight = React.useMemo(() => {
        const allWords = page.paragraphs.flatMap(p => p.wordsToHighlight || []);
        return [...new Set(allWords)];
    }, [page.paragraphs]);

    const highlightRegex = React.useMemo(() => {
        if (wordsToHighlight.length === 0) {
            return null;
        }
        return new RegExp(`(${wordsToHighlight.join('|')})`, 'g');
    }, [wordsToHighlight]);

    const renderHighlightedText = (text: string) => {
        if (!highlightRegex) {
            return text;
        }

        const parts = text.split(highlightRegex);

        return parts.map((part, index) =>
            // Every other part in the split array is a matched highlighted word.
            index % 2 === 1
                ? <span key={index} className={styles.highlighted}>{part}</span>
                : part
        );
    };

    return(
        <div>
            {page.imageUrl && <img src={page.imageUrl} alt="Story illustration" style={{ maxWidth: '100%', marginBottom: '1rem' }} />}
            {page.paragraphs.map(p => (
                <p key={p.id}>
                    {renderHighlightedText(p.content)}
                </p>
            ))}

            {page.vocabulary && page.vocabulary.length > 0 && (
                <div className="mt-4 pt-3 border-top">
                    <h6>New Words</h6>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }} className="small">
                        {page.vocabulary.map((vocabItem, index) =>(
                            <span key={vocabItem.id ?? index}><strong>{vocabItem.word}</strong>= {vocabItem.translation}</span>
                        ))}
                    </div>
                </div>
            )}

        </div>
    );
};

export default StoryContentPage;
