import { Outlet } from 'react-router-dom';
import { LanguageSettingsProvider } from '../contexts/LanguageSettingsContext';
import { BookProvider } from '../contexts/BookContext';

/**
 * A layout component that provides the necessary contexts for the study-related pages.
 */
const StudyLayout = () => {
  return (
    <LanguageSettingsProvider>
      <BookProvider>
        <Outlet />
      </BookProvider>
    </LanguageSettingsProvider>
  );
};

export default StudyLayout;