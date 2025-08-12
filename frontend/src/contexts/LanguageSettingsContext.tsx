import React, { createContext, useState, useContext, useMemo } from 'react';

interface LanguageSettingsContextType {
    language: string;
    setLanguage: React.Dispatch<React.SetStateAction<string>>;
    difficulty: string;
    setDifficulty: React.Dispatch<React.SetStateAction<string>>;
    languageName: string;
}

const LanguageSettingsContext = createContext<LanguageSettingsContextType | undefined>(undefined);

export const useLanguage = (): LanguageSettingsContextType => {
    const context = useContext(LanguageSettingsContext);
    if(context === undefined) {
        throw new Error('useLanguage must be within a LanguageProvider');
    }
    return context;
};

interface LanguageSettingsProviderProps {
    children: React.ReactNode;
}

export const LanguageSettingsProvider: React.FC<LanguageSettingsProviderProps> = ({ children }) => {
    const [language, setLanguage] = useState('Korean');
    const [difficulty, setDifficulty] = useState('Beginner');

    const languageMap: Record<string, string> = {
        'Korean': '한국어',
        'Japanese': '日本語'
    };

    // `languageName` is now derived directly from the `language` state.
    // This is simpler and prevents the state from ever being out of sync.
    const languageName = languageMap[language] || language;

    const value = useMemo(() => ({
        language,
        setLanguage,
        difficulty,
        setDifficulty,
        languageName,
    }), [language, difficulty, languageName]);

    return (
        <LanguageSettingsContext.Provider value={value}>
            {children}
        </LanguageSettingsContext.Provider>
    );
};