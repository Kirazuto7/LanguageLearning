import {StoryContentPageDTO} from "../../../../shared/types/dto";
import React, {useState} from "react";
import styles from "./storycontentpage.module.scss";
import {Clipboard, ClipboardCheck, VolumeUpFill} from "react-bootstrap-icons";
import {useSettingsManager} from "../../../userSettings/hooks/useSettingsManager";
import useTextToSpeech from "../../../../hooks/useTextToSpeech";
import {mascotGenders, MascotName} from "../../../../shared/types/types";
import {getPhoneticText} from "../../../../shared/utils/textUtils";

interface StoryContentPageProps {
    page: StoryContentPageDTO;
}

const StoryContentPage: React.FC<StoryContentPageProps> = ({ page }) => {
    const [isCopied, setIsCopied] = useState(false);
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();

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

    // 1. Prepare the full text of the story page for copying.
    const fullTextToCopy = React.useMemo(() => {
        return page.paragraphs.map(p => p.content).join('\n\n');
    }, [page.paragraphs]);

    // 2. Handly the copy action.
    const handleCopyClick = () => {
        if (fullTextToCopy) {
            navigator.clipboard.writeText(fullTextToCopy).then(() => {
                setIsCopied(true);
                setTimeout(() => setIsCopied(false), 2000);
            })
            .catch(err => {
                console.error('Failed to copy text: ', err);
            });
        }
    };

    const handleSpeak = () => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            const isJapanese = settings.language.toLowerCase() === "japanese";
            const textToSpeech = isJapanese ? getPhoneticText(fullTextToCopy) : fullTextToCopy;
            cancel();

            speak(textToSpeech, settings.language, gender);
        }
    };

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

            <div className={styles.contentWrapper}>
                <button
                    onClick={handleSpeak}
                    className={`btn ${styles.speakButton}`}
                    title="Read story content"
                >
                    <VolumeUpFill/>
                </button>
                <button
                    onClick={handleCopyClick}
                    className={`btn ${styles.copyButton}`}
                    title={isCopied ? "Copied!" : "Copy story text"}
                >
                    {isCopied ? <ClipboardCheck/> : <Clipboard/>}
                </button>

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
        </div>
    );
};

export default StoryContentPage;
