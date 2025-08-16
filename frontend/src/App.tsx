import { Routes, Route } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import './App.css';
import NavigationBar from './pages/NavigationBar';
import HomePage from './pages/HomePage';
import StudyBookPage from './pages/StudyBookPage';
import LandingPage from './pages/LandingPage';
import LoginPage  from './pages/LoginPage';

const App: React.FC = () => {

  return (
      <div className="App">
        <NavigationBar />

        <main className="mt-4">
          <Container>
            <Routes>
              <Route path="/" element={<LandingPage/>} />
              <Route path="/login" element={<LoginPage/>} />
              <Route path="/home" element={<HomePage />} />
              <Route path="/study" element={<StudyBookPage />} />
            </Routes>
          </Container>
        </main>
      </div>
  );
}

export default App;
