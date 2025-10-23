import { FlagIconCode } from "react-flag-kit";

/**
 * Maps full language names to their corresponding ISO 3166-1 alpha-2 country codes.
 * This is used for displaying flags.
 */
const languageToCountryCode: { [key: string]: FlagIconCode } = {
    english: 'US',
    spanish: 'ES',
    french: 'FR',
    german: 'DE',
    italian: 'IT',
    japanese: 'JP',
    korean: 'KR',
    chinese: 'CN',
    thai: 'TH',
    // Add other languages as needed
};

/**
 * Gets the country code for a given language.
 * @param language The full language name (e.g., 'English').
 * @returns The two-letter country code or undefined if not found.
 */
export const getCountryCodeForLanguage = (language: string): FlagIconCode | undefined => {
    return languageToCountryCode[language.toLowerCase()];
};