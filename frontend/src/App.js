import { Routes, Route, Link } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import './App.css';
import LearnPage from './pages/LearnPage';
import { useBookManager } from './hooks/useBookManager';
import { BookProvider } from './contexts/BookContext';
import { LanguageProvider } from './contexts/LanguageContext';

function Home() {
  return (
    <div className="p-5 mb-4 bg-light rounded-3">
      <Container fluid className="py-5">
        <h1 className="display-5 fw-bold">Welcome!</h1>
        <p className="col-md-8 fs-4">
          This is a collection of tools to aid in language learning.
          Select a tool from the navigation bar to get started.
        </p>
      </Container>
    </div>
  );
}

function App() {

  const bookManager = useBookManager();

  return (
    <LanguageProvider>
      <BookProvider {...bookManager}>
        <div className="App">
          <Navbar className="custom-navbar" variant="dark" expand="lg">
            <Container>
              <Navbar.Brand as={Link} to="/">Language Learning Tools</Navbar.Brand>
              <Navbar.Toggle aria-controls="basic-navbar-nav" />
              <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="me-auto">
                  <Nav.Link as={Link} to="/">Home</Nav.Link>
                  <Nav.Link as={Link} to="/learn">Learn</Nav.Link>
                </Nav>
              </Navbar.Collapse>
            </Container>
          </Navbar>

          <main className="mt-4">
            <Container>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/learn" element={<LearnPage />} />
              </Routes>
            </Container>
          </main>
        </div>
      </BookProvider>
    </LanguageProvider>
  );
}

export default App;
