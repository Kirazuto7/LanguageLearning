import { Outlet } from 'react-router-dom';
import { LanguageSettingsProvider } from '../contexts/LanguageSettingsContext';

/**
 * A layout component that provides the necessary contexts for the study-related pages.
 */
const StudyLayout = () => {
  return (
    <LanguageSettingsProvider>
        <Outlet />
    </LanguageSettingsProvider>
  );
};

export default StudyLayout;