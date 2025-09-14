/**
 * Extracts the phonetic reading from a string that may contain HTML ruby tags.
 * This is used to prepare Japanese text for Text-to-Speech services.
 * It replaces `<ruby>BASE<rt>KANA</rt></ruby>` with just KANA.
 * @param text The text containing ruby tags.
 * @returns A string with only the phonetic reading.
 */
export const getPhoneticText = (text: string): string => {
    // This regex finds ruby tags and replaces the entire tag with the content of the <rt> tag.
    // A second replace is added to strip any other stray HTML tags.
    return text.replace(/<ruby>.*?<rt>(.*?)<\/rt><\/ruby>/g, '$1').replace(/<[^>]*>/g, '');
};

/**
 * Formats a string into title case, capitalizing the first letter of each word.
 * @param text The input string.
 * @returns The formatted string.
 */
export const formatText = (text: string): string => {
    return text.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
};