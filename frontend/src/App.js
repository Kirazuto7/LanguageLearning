import { Routes, Route, Link } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import './App.css';
import HomePage from './pages/HomePage';
import StudyBookPage from './pages/StudyBookPage';
import { useBookManager } from './hooks/useBookManager';
import { BookProvider } from './contexts/BookContext';
import { LanguageProvider } from './contexts/LanguageContext';

function App() {

  const bookManager = useBookManager();

  return (
    <LanguageProvider>
      <BookProvider {...bookManager}>
        <div className="App">
          <Navbar className="custom-navbar" variant="dark" expand="lg">
            <Container>
              <Navbar.Brand as={Link} to="/">LangMaster</Navbar.Brand>
              <Navbar.Toggle aria-controls="basic-navbar-nav" />
              <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="me-auto">
                  <Nav.Link as={Link} to="/">Home</Nav.Link>
                  <Nav.Link as={Link} to="/study">Study</Nav.Link>
                </Nav>
              </Navbar.Collapse>
            </Container>
          </Navbar>

          <main className="mt-4">
            <Container>
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/study" element={<StudyBookPage />} />
              </Routes>
            </Container>
          </main>
        </div>
      </BookProvider>
    </LanguageProvider>
  );
}

export default App;
