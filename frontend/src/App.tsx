import { Routes, Route } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import './App.scss';
import NavigationBar from './components/navbar/NavigationBar';
import HomePage from './pages/home/HomePage';
import StudyBookPage from './pages/StudyBookPage';
import LandingPage from './pages/home/LandingPage';
import LoginPage  from './pages/login/LoginPage';
import SessionManager from './components/headless/SessionManager';
import BackgroundLayout from "./layouts/BackgroundLayout";

const App: React.FC = () => {

  return (
      <>
        <SessionManager/>
        <NavigationBar />

        <Routes>
          <Route element={<BackgroundLayout/>}>
              <Route path="/" element={<LandingPage/>} />

              <Route path="/home" element={<HomePage />} />
              <Route path="/study" element={<StudyBookPage />} />
          </Route>
          <Route path="/login" element={<LoginPage/>} />
        </Routes>
      </>
  );
}

export default App;
