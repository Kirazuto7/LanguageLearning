import {createContext, ReactNode, useContext, useState} from "react";

type Theme = 'light' | 'sunset' | 'nebula';

interface ThemeContextType {
    theme: Theme;
    setTheme: (theme: Theme) => void;
}

interface ThemeProviderProps {
    children: ReactNode;
}

const ThemeContext = createContext<ThemeContextType| undefined>(undefined);

export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
    const [theme, setTheme] = useState<Theme>('nebula');

    return(
        <ThemeContext.Provider value={{theme, setTheme}}>
            {children}
        </ThemeContext.Provider>
    );
};

export const useTheme = () => {
    const context = useContext(ThemeContext);
    if(context === undefined) {
        throw new Error('Component with useTheme must be used within a ThemeProvider');
    }
    return context;
}
