import {StoryContentPageDTO} from "../../../../shared/types/dto";
import React from "react";
import styles from "./storycontentpage.module.scss";

interface StoryContentPageProps {
    page: StoryContentPageDTO;
}

const StoryContentPage: React.FC<StoryContentPageProps> = ({ page }) => {
    const vocabWords = React.useMemo(() => new Set(page.vocabulary.map(vocabItem => vocabItem.word)), [page.vocabulary]);

    const renderHighlightedText = (text: string) => {
        if (!vocabWords.size) {
            return text;
        }

        // Find all vocabulary words in the text and highlight them
        const regex = new RegExp(`(${Array.from(vocabWords).join('|')})`, 'g');
        const parts = text.split(regex);

        return parts.map((part, index) =>
            vocabWords.has(part)
            ? <span key={index} className={styles.highlighted}>{part}</span>
            : (part)
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