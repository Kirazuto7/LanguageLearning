const storyBookDesigns = [
    'design-ornate',
    'design-geometric',
    'design-mystical',
    'design-emblem',
    'design-art-deco'
];

/**
 * Gets a consistent, deterministic design class name for a storybook based on its unique ID.
 * @param bookId The unique ID of the book.
 * @returns A CSS class name for the book design.
 */
export const getStoryBookDesign = (bookId: number): string => {
    // If there are no designs, return an empty string to prevent errors.
    if (storyBookDesigns.length === 0) {
        return '';
    }

    // Use the modulo operator to get a consistent index from 0 to the number of designs.
    // This ensures the same book ID always gets the same design.
    const designIndex = bookId % storyBookDesigns.length;

    return storyBookDesigns[designIndex];
};