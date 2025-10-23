declare module 'leo-profanity' {
    const leoProfanity: {
        loadDictionary(): void;
        add(words: string[]): void;
        check(text: string): boolean;
        clean(text: string, replacement?: string): string;
    };
    export default leoProfanity;
}