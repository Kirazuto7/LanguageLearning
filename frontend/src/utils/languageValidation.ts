/**
 * A dictionary mapping languages to a RegExp that matches characters NOT allowed for that language.
 * This allows for easy scalability by just adding new language entries.
 */

 export const LANGUAGE_INPUT_VALIDATION: { [key: string]: RegExp } = {
    'japanese': /[^\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FAF\u3000-\u303F\s.?!,]/g,
    'korean': /[^\uAC00-\uD7A3\u1100-\u11FF\u3130-\u318F\s.?!,]/g,
    'chinese': /[^\u4e00-\u9fa5\u3000-\u303f\s.?!,]/g,
    'thai': /[^\u0E00-\u0E7F\s.?!,]/g,
    'italian': /[^a-zA-Z\u00C0-\u00FF\s'.?!,]/g,
    'french': /[^a-zA-Z\u00C0-\u00FF\s'.?!,]/g,
    'spanish': /[^a-zA-Z\u00C0-\u00FF\s'.?!,]/g,
    'german': /[^a-zA-Z\u00C0-\u00FF\s'.?!,]/g,
 }

 export const filterInputByLanguage = (rawValue: string, language: string | undefined): string => {
    if (!language) return rawValue;

    const invalidCharsRegex = LANGUAGE_INPUT_VALIDATION[language.toLowerCase()];
    return invalidCharsRegex ? rawValue.replace(invalidCharsRegex, '') : rawValue;
 }