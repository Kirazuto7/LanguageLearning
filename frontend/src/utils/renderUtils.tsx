import { WordDTO } from '../types/dto';

export const renderWord = (word: WordDTO): JSX.Element => {
    let content: JSX.Element;

    if (word.language.toLowerCase() === 'korean') {
        // Korean is phonetic and does not use ruby tags.
        content = <>{word.nativeWord}</>;
    }
    else if (word.language.toLowerCase() === 'japanese') {
        // For Japanese, use the 'details' field to decide how to render.
        // The <ruby> tag is only appropriate for Kanji with furigana.
        if (word.details?.script === 'kanji' && word.phoneticSpelling) {
            content = (
                <ruby>
                    {word.nativeWord}
                    <rt>{word.phoneticSpelling}</rt>
                </ruby>
            );
        } else {
            // For Hiragana, Katakana, or Kanji without furigana, render the native word directly.
            content = <>{word.nativeWord}</>;
        }
    } else {
        // Fallback for any other language
        content = <>{word.nativeWord || 'N/A'}</>;
    }

    // Return a complete table cell element for valid HTML structure.
    return <td>{content}</td>;
}