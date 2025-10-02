import {Routes, Route} from 'react-router-dom';
import React, { lazy, Suspense } from "react";
import './App.scss';
import NavigationBar from '../widgets/navigationBar/NavigationBar';
import ProgressSubscriptionManager from "../widgets/progressBar/ProgressSubscriptionManager";
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

const LandingPage = lazy(() => import('../pages/home/LandingPage'));
const HomePage = lazy(() => import('../pages/home/HomePage'));
const LessonBookPage = lazy(() => import('../pages/lessontools/LessonBookPage'));
const StoryBookPage = lazy(() => import('../pages/lessontools/StoryBookPage'));

const App: React.FC = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
      <>
        <SessionManager/>
        <ProgressSubscriptionManager/>
        <ThemeManager />
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
