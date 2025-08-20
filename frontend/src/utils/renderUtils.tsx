import { WordDTO } from '../types/dto';

/**
 * Checks if a string contains any Japanese Kanji characters.
 * @param text The string to check.
 * @returns True if the string contains Kanji, false otherwise.
 */
export const containsKanji = (text: string): boolean => /[\u4e00-\u9faf]/.test(text);

export const renderWord = (word: WordDTO): JSX.Element => {
    let content: JSX.Element;

    if (word.language.toLowerCase() === 'korean') {
        // Korean is phonetic and does not use ruby tags.
        content = <>{word.nativeWord}</>;
    }
    else if (word.language.toLowerCase() === 'japanese') {
        // For Japanese, use the 'details' field to decide how to render.
        // The <ruby> tag is only appropriate for Kanji with furigana.
        // A more robust check is to see if the native word contains Kanji characters.
        if (word.nativeWord && containsKanji(word.nativeWord) && word.phoneticSpelling) {
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