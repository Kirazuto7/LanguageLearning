import React, { createContext, useState, useContext } from 'react';

const LanguageContext = createContext();

export const useLanguage = () => {
    return useContext(LanguageContext);
};

export const LanguageProvider = ({ children }) => {
    const [language, setLanguage] = useState('Korean');
    const [level, setLevel] = useState('Beginner');

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
        level,
        setLevel,
        languageName,
    };

    return (
        <LanguageContext.Provider value={value}>
            {children}
        </LanguageContext.Provider>
    );
};