import {Routes, Route, useLocation} from 'react-router-dom';
import React, { useEffect, lazy, Suspense } from "react";
import './App.scss';
import NavigationBar from './components/navbar/NavigationBar';
import SessionManager from './components/headless/SessionManager';
import BackgroundLayout from "./layouts/BackgroundLayout";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import {useSelector} from "react-redux";
import {selectCurrentTheme} from "./features/state/settingsSlice";
import { Container, Spinner } from "react-bootstrap";

const LandingPage = lazy(() => import('./pages/home/LandingPage'));
const LoginPage = lazy(() => import('./pages/login/LoginPage'));
const HomePage = lazy(() => import('./pages/home/HomePage'));
const StudyBookPage = lazy(() => import('./pages/lessontools/StudyBookPage'));

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
