import { WordDTO } from '../types/dto';

export const renderWord = (word: WordDTO): JSX.Element => {
    const { details } = word;
    let content: JSX.Element;

    switch (details.__typename) {
        case 'JapaneseWordDetails':
            if (details.kanji && details.hiragana) {
                content = (
                    <ruby>
                        {details.kanji}
                        <rt>{details.hiragana}</rt>
                    </ruby>
                );
            }
            else {
                // For Hiragana, Katakana, or Kanji without furigana, render the native word directly.
                content = <>{details.hiragana || details.katakana || details.romaji}</>;
            }
            break;
        case 'GenericWordDetails':
            content = <>{details.nativeWord}</>;
            break;
        default:
            content = <>N/A</>;
    }

    // Return a complete table cell element for valid HTML structure.
    return <td>{content}</td>;
}