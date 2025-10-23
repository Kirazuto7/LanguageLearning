import {Routes, Route} from 'react-router-dom';
import React, { useState } from "react";
import './App.scss';
import NavigationBar from '../widgets/navigationBar/NavigationBar';
import SessionManager from './SessionManager';
import BackgroundLayout from "../shared/layouts/BackgroundLayout";
import ProtectedRoute from "../features/authentication/components/ProtectedRoute";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../features/authentication/authSlice";
import FullScreenLoader from "../shared/components/fullscreenLoader/FullScreenLoader";

import '@fontsource/inter/400.css'; // Regular
import '@fontsource/inter/500.css'; // Medium
import '@fontsource/inter/600.css'; // Semi-Bold
import '@fontsource/inter/700.css'; // Bold
import '../shared/assets/_global.scss';
import ThemeManager from "./ThemeManager";
import LoginPage from "../pages/login/LoginPage";
import { TranslationToolButton } from "../widgets/translationTool/components";
import SessionSynchronizer from "./SessionSynchronizer";

// Eagerly import all page components to prevent chunk load errors
import LandingPage from '../pages/home/LandingPage';
import DashboardPage from '../pages/home/./DashboardPage';
import LessonBookPage from '../pages/lessontools/LessonBookPage';
import StoryBookPage from '../pages/lessontools/StoryBookPage';
import OidcOnboarding from '../features/authentication/components/OidcOnboarding';

const App: React.FC = () => {
    const [isSessionChecked, setIsSessionChecked] = useState(false);
    const isAuthenticated = useSelector(selectIsAuthenticated);

    if (!isSessionChecked) {
        return (
            <>
                <ThemeManager />
                <SessionSynchronizer onSyncComplete={() => setIsSessionChecked(true)} />
                <FullScreenLoader />
            </>
        );
    }

  return (
      <>
        <SessionManager/>
        <ThemeManager />
        <NavigationBar />

        <Routes>
            {isAuthenticated ? (
                <Route element={<ProtectedRoute/>}>
                    <Route element={<BackgroundLayout/>}>
                        <Route index element={<DashboardPage />} />
                        <Route path="/home" element={<DashboardPage />} />
                        <Route path="/study" element={<LessonBookPage />} />
                        <Route path="/read" element={<StoryBookPage />} />
                        {/* Add a catch-all to redirect to home if authenticated and on a public route */}
                        <Route path="*" element={<DashboardPage />} />
                    </Route>
                </Route>
            ) : (
                <>
                    <Route path="/welcome" element={<LandingPage/>} />
                    <Route path="/login" element={<LoginPage/>} />
                    <Route path="/welcome/oidc" element={<OidcOnboarding/>} />
                    {/* Add a catch-all to redirect to welcome if not authenticated */}
                    <Route path="*" element={<LandingPage />} />
                </>
            )}
        </Routes>

          {isAuthenticated && <TranslationToolButton/>}
      </>
  );
}

export default App;
