import { difficulties } from "../types/options";

/**
 * Selects a CSS variable color based on the difficulty string.
 * @param difficulty The difficulty string (e.g., 'beginner', 'intermediate').
 * @returns A CSS variable name for the difficulty color.
 */
export const getDifficultyColor = (difficulty: string): string => {
    const lowerCaseDifficulty = difficulty.toLowerCase();

    // Check if the provided difficulty is one of the valid options.
    const isValidDifficulty = difficulties.some(d => d.value.toLowerCase() === lowerCaseDifficulty);

    if (isValidDifficulty) {
        // Dynamically construct the variable name, e.g., 'pre-intermediate' -> '--difficulty-pre-intermediate'
        const cssVarName = `--difficulty-${lowerCaseDifficulty.replace(/ /g, '-')}`;
        return `var(${cssVarName})`;
    } else {
        // Fallback to the default color if the difficulty is not recognized.
        return 'var(--book-cover-color)';
    }
};

/**
 * Selects a CSS variable color for a storybook based on its difficulty.
 * @param difficulty The difficulty string.
 * @returns A CSS variable name for the storybook color.
 */
export const getStoryBookDifficultyColor = (difficulty: string): string => {
    const lowerCaseDifficulty = difficulty.toLowerCase();
    const isValidDifficulty = difficulties.some(d => d.value.toLowerCase() === lowerCaseDifficulty);

    if (isValidDifficulty) {
        const cssVarName = `--storybook-difficulty-${lowerCaseDifficulty.replace(/ /g, '-')}`;
        return `var(${cssVarName})`;
    } else {
        return 'var(--book-cover-color)';
    }
};