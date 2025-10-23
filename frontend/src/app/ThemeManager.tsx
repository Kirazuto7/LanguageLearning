import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { selectCurrentTheme } from "../features/userSettings/settingsSlice";
import { selectIsAuthenticated } from "../features/authentication/authSlice";
import { protectedPaths } from "../shared/types/options";

/**
 * A component that manages the application's theme by listening to Redux state and
 * applying the correct class to the document body.
 */
 const ThemeManager: React.FC = () => {
    const location = useLocation();
    const userTheme = useSelector(selectCurrentTheme);
    const isAuthenticated = useSelector(selectIsAuthenticated);

    useEffect(() => {
        const isThemedPage = isAuthenticated && protectedPaths.some(path => location.pathname.startsWith(path));
        const themeToApply = isThemedPage ? userTheme : 'default';

        document.body.className = `themed-body ${themeToApply}`;
    }, [location, userTheme, isAuthenticated]);

    return null;
 };

 export default ThemeManager;