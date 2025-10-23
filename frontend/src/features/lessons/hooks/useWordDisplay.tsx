import { WordDTO } from '../../../shared/types/dto';
import React from 'react';

/**
 * A custom hook to determine how to display word details based on the language.
 * @param word - The word data transfer object.
 * @returns An object containing `nativeWordDisplay`, `textToSpeak`, and `detailsDisplay`.
 */
export const useWordDisplay = (word: WordDTO) => {
    const { details } = word;

    let nativeWordDisplay: string | JSX.Element = '';
    let textToSpeak: string = '';
    let detailsDisplay: JSX.Element | null = null;

    switch (details?.__typename) {
        case 'JapaneseWordDetails':
            textToSpeak = details.hiragana;

            if (details.kanji && details.kanji !== details.hiragana) {
                nativeWordDisplay = <ruby>{details.kanji}<rt>{details.hiragana}</rt></ruby>;
            } else if (details.katakana) {
                nativeWordDisplay = details.katakana;
            } else {
                nativeWordDisplay = details.hiragana;
            }
            detailsDisplay = (
                <>
                    {details.kanji && <p><strong>Kanji:</strong> {details.kanji}</p>}
                    {details.hiragana && <p><strong>Hiragana:</strong> {details.hiragana}</p>}
                    {details.katakana && <p><strong>Katakana:</strong> {details.katakana}</p>}
                    {details.romaji && <p><strong>Romaji:</strong> {details.romaji}</p>}
                </>
            );
            break;
        case 'KoreanWordDetails':
            nativeWordDisplay = details.hangul;
            textToSpeak = details.hangul;
            detailsDisplay = (
                <>
                    {details.hangul && <p><strong>Hangul:</strong> {details.hangul}</p>}
                    {details.hanja && <p><strong>Hanja:</strong> {details.hanja}</p>}
                    {details.romaja && <p><strong>Romaja:</strong> {details.romaja}</p>}
                </>
            );
            break;
        case 'ChineseWordDetails':
            nativeWordDisplay = details.pinyin ? (
                <ruby>{details.simplified}<rt>{details.pinyin}</rt></ruby>
            ) : (
                details.simplified
            );
            textToSpeak = details.simplified;
            detailsDisplay = (
                <>
                    {details.simplified && <p><strong>Simplified:</strong> {details.simplified}</p>}
                    {details.traditional && <p><strong>Traditional:</strong> {details.traditional}</p>}
                    {details.pinyin && <p><strong>Pinyin:</strong> {details.pinyin}</p>}
                    {details.toneNumber && <p><strong>Tone:</strong> {details.toneNumber}</p>}
                </>
            );
            break;
        case 'ThaiWordDetails':
            nativeWordDisplay = details.thaiScript;
            textToSpeak = details.thaiScript;
            detailsDisplay = (
                <>
                    {details.thaiScript && <p><strong>Thai Script:</strong> {details.thaiScript}</p>}
                    {details.romanization && <p><strong>Romanization:</strong> {details.romanization}</p>}
                    {details.tonePattern && <p><strong>Tone:</strong> {details.tonePattern}</p>}
                </>
            );
            break;
        case 'ItalianWordDetails':
        case 'SpanishWordDetails':
        case 'FrenchWordDetails':
        case 'GermanWordDetails':
            nativeWordDisplay = details.lemma;
            textToSpeak = details.lemma;
            detailsDisplay = (
                <>
                    {details.lemma && <p><strong>Lemma:</strong> {details.lemma}</p>}
                    {details.gender && <p><strong>Gender:</strong> {details.gender}</p>}
                    {details.pluralForm && <p><strong>Plural:</strong> {details.pluralForm}</p>}
                    {details.__typename === 'GermanWordDetails' && details.separablePrefix && <p><strong>Prefix:</strong> {details.separablePrefix}</p>}
                </>
            );
            break;
    }

    return { nativeWordDisplay, textToSpeak, detailsDisplay };
};