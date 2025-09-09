import {Routes, Route, useLocation} from 'react-router-dom';
import React, { useEffect, lazy, Suspense } from "react";
import './App.scss';
import NavigationBar from '../widgets/navigationBar/NavigationBar';
import SessionManager from './SessionManager';
import BackgroundLayout from "../shared/layouts/BackgroundLayout";
import ProtectedRoute from "../features/authentication/components/ProtectedRoute";
import {useSelector} from "react-redux";
import {selectCurrentTheme} from "../features/userSettings/settingsSlice";
import { Container, Spinner } from "react-bootstrap";

import '@fontsource/inter/400.css'; // Regular
import '@fontsource/inter/500.css'; // Medium
import '@fontsource/inter/600.css'; // Semi-Bold
import '@fontsource/inter/700.css'; // Bold
import '../shared/assets/_global.scss';
import LoginPage from "../pages/login/LoginPage";
const LandingPage = lazy(() => import('../pages/home/LandingPage'));
const HomePage = lazy(() => import('../pages/home/HomePage'));
const StudyBookPage = lazy(() => import('../pages/lessontools/LessonBookPage'));

const App: React.FC = () => {
    const location = useLocation();
    const userTheme = useSelector(selectCurrentTheme);

    const themedPaths = ['/home', '/study', '/read'];

    useEffect(() => {
        const isThemedPage = themedPaths.some(path => location.pathname.startsWith(path));
        const themeToApply = isThemedPage ? userTheme : 'default';

        document.body.className = `themed-body ${themeToApply}`;
    }, [location, userTheme]);
  return (
      <>
        <SessionManager/>
        <NavigationBar />

        <Suspense fallback={
            <Container
                className="d-flex justify-content-center align-items-center"
                style={{ minHeight: '100vh'}}
            >
                <Spinner animation="border" />
            </Container>
        }>
            <Routes>
                <Route path="/" element={<LandingPage/>} />
                <Route path="/login" element={<LoginPage/>} />

                <Route element={<ProtectedRoute/>}>
                    <Route element={<BackgroundLayout/>}>
                        <Route path="/home" element={<HomePage />} />
                        <Route path="/study" element={<StudyBookPage />} />
                    </Route>
                </Route>
            </Routes>
        </Suspense>
      </>
  );
}

export default App;
