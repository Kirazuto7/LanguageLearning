import { WordDTO } from '../types/dto';

export const renderWord = (word: WordDTO): JSX.Element => {
    const { details } = word; // details can be undefined if not queried
    let content: JSX.Element | string;

    if (!details) {
        content = 'N/A';
    } else {
        switch (details.__typename) {
            case 'JapaneseWordDetails':
                if (details.kanji && details.hiragana) {
                    content = (
                        <ruby>
                            {details.kanji}
                            <rt>{details.hiragana}</rt>
                        </ruby>
                    );
                } else {
                    content = details.hiragana || details.katakana || '';
                }
                break;
            case 'KoreanWordDetails':
                content = details.hangul;
                break;
            case 'ChineseWordDetails':
                content = (
                    <ruby>
                        {details.simplified}
                        <rt>{details.pinyin}</rt>
                    </ruby>
                );
                break;
            case 'ThaiWordDetails':
                content = details.thaiScript;
                break;
            case 'ItalianWordDetails':
            case 'SpanishWordDetails':
            case 'FrenchWordDetails':
            case 'GermanWordDetails':
                content = details.lemma;
                break;
            default:
                content = 'N/A';
        }
    }

    // Return a complete table cell element for valid HTML structure.
    return <td>{content}</td>;
}