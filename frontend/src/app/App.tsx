import {Routes, Route, useLocation} from 'react-router-dom';
import React, { useEffect, lazy, Suspense } from "react";
import './App.scss';
import NavigationBar from '../widgets/navigationBar/NavigationBar';
import SessionManager from './SessionManager';
import BackgroundLayout from "../shared/layouts/BackgroundLayout";
import ProtectedRoute from "../features/authentication/components/ProtectedRoute";
import { useSelector } from "react-redux";
import { selectCurrentTheme } from "../features/userSettings/settingsSlice";
import { selectIsAuthenticated } from "../features/authentication/authSlice";
import FullScreenLoader from "../shared/components/fullscreenLoader/FullScreenLoader";

import '@fontsource/inter/400.css'; // Regular
import '@fontsource/inter/500.css'; // Medium
import '@fontsource/inter/600.css'; // Semi-Bold
import '@fontsource/inter/700.css'; // Bold
import '../shared/assets/_global.scss';
import LoginPage from "../pages/login/LoginPage";
import { TranslationToolButton } from "../widgets/translationTool/components";

const LandingPage = lazy(() => import('../pages/home/LandingPage'));
const HomePage = lazy(() => import('../pages/home/HomePage'));
const LessonBookPage = lazy(() => import('../pages/lessontools/LessonBookPage'));
const StoryBookPage = lazy(() => import('../pages/lessontools/StoryBookPage'));

const themedPaths = ['/home', '/study', '/read'];

const App: React.FC = () => {
    const location = useLocation();
    const userTheme = useSelector(selectCurrentTheme);
    const isAuthenticated = useSelector(selectIsAuthenticated);

    useEffect(() => {
        const isThemedPage = themedPaths.some(path => location.pathname.startsWith(path));
        const themeToApply = isThemedPage ? userTheme : 'default';

        document.body.className = `themed-body ${themeToApply}`;
    }, [location, userTheme]);
  return (
      <>
        <SessionManager/>
        <NavigationBar />

        <Suspense fallback={<FullScreenLoader/>}>
            <Routes>
                <Route path="/" element={<LandingPage/>} />
                <Route path="/login" element={<LoginPage/>} />

                <Route element={<ProtectedRoute/>}>
                    <Route element={<BackgroundLayout/>}>
                        <Route path="/home" element={<HomePage />} />
                        <Route path="/study" element={<LessonBookPage />} />
                        <Route path="/read" element={<StoryBookPage />} />
                    </Route>
                </Route>
            </Routes>
        </Suspense>

          {isAuthenticated && <TranslationToolButton/>}
      </>
  );
}

export default App;
