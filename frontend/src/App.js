import { Routes, Route, Link } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import './App.css';
import ChapterGenerator from './components/ChapterGenerator';
import Mascot from './components/Mascot';

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
  return (
    <div className="App">
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand as={Link} to="/">Language Learning Tools</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link as={Link} to="/">Home</Nav.Link>
              <Nav.Link as={Link} to="/generator">Chapter Generator</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <main className="mt-4">
        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/generator" element={<ChapterGenerator />} />
          </Routes>
          <Mascot />
        </Container>
      </main>
    </div>
  );
}

export default App;
