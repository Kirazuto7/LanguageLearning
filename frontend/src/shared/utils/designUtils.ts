const storyBookDesigns = [
    'design-ornate',
    'design-geometric',
    'design-classic',
];

/**
 * Selects a design class from the palette deterministically based on a string (e.g., title).
 * @param str The string to use for design selection.
 * @returns A CSS class name for the book design.
 */
export const getStoryBookDesign = (str: string): string => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash % storyBookDesigns.length);
    return storyBookDesigns[index];
};