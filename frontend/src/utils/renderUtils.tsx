import { AnyWordDTO } from '../types/dto';

export const renderWord = (word: AnyWordDTO): JSX.Element => {
    if (word.type === 'korean') {
        return <>{word.hangeul}</>;
    }
    else if (word.type === 'japanese') {
        // The <ruby> tag shows furigana
        if(word.kanji && word.hiragana) {
            return(
                <ruby>
                    {word.kanji}
                    <rt>{word.hiragana}</rt>
                </ruby>
            );
        }
        // Default for Japanese
        return <>{word.hiragana || word.katakana || word.romaji}</>;
    }
    // Unknown word type fallback
    return <>N/A</>;
}