import {createContext, ReactNode, useContext} from "react";
import {useSelector} from "react-redux";
import {selectCurrentTheme} from "../features/state/settingsSlice";

export type Theme = 'default' | 'light' | 'sunset' | 'nebula' | 'cafe' | 'hanok' | 'fuji' | 'school';

interface ThemeContextType {
    theme: Theme;
}

interface ThemeProviderProps {
    children: ReactNode;
}

const ThemeContext = createContext<ThemeContextType| undefined>(undefined);

export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
    const theme = useSelector(selectCurrentTheme) as Theme;

    return(
        <ThemeContext.Provider value={{theme}}>
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
