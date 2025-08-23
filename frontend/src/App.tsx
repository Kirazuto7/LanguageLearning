import {Routes, Route, useLocation} from 'react-router-dom';
import React, { useEffect} from "react";
import './App.scss';
import NavigationBar from './components/navbar/NavigationBar';
import HomePage from './pages/home/HomePage';
import StudyBookPage from './pages/lessontools/StudyBookPage';
import LandingPage from './pages/home/LandingPage';
import LoginPage  from './pages/login/LoginPage';
import SessionManager from './components/headless/SessionManager';
import BackgroundLayout from "./layouts/BackgroundLayout";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import {useSelector} from "react-redux";
import {selectCurrentTheme} from "./features/state/settingsSlice";

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
      </>
  );
}

export default App;
