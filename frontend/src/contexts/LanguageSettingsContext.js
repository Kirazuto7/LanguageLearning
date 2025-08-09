import React, { createContext, useState, useContext } from 'react';

const LanguageSettingsContext = createContext();

export const useLanguage = () => {
    return useContext(LanguageSettingsContext);
};

export const LanguageSettingsProvider = ({ children }) => {
    const [language, setLanguage] = useState('Korean');
    const [difficulty, setDifficulty] = useState('Beginner');

    const languageMap = {
        'Korean': '한국어',
        'Japanese': '日本語'
    };

    // `languageName` is now derived directly from the `language` state.
    // This is simpler and prevents the state from ever being out of sync.
    const languageName = languageMap[language] || language;

    const value = {
        language,
        setLanguage,
        difficulty,
        setDifficulty,
        languageName,
    };

    return (
        <LanguageSettingsContext.Provider value={value}>
            {children}
        </LanguageSettingsContext.Provider>
    );
};