import { Link, useNavigate } from 'react-router-dom';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import { Button } from 'react-bootstrap';
import { useLogoutMutation } from '../features/api/userApiSlice';
import { useSelector } from 'react-redux';
import { RootState } from '../app/store';

const NavigationBar = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [logout] = useLogoutMutation();
  const navigate = useNavigate();
    const handleLogout = async() => {
      try {
        console.log("Logging out...");
        await logout().unwrap();
        navigate('/login');
      }
      catch(err) {
        console.error('Failed to logout.', err);
      }
    }

    const renderDefaultNavigation = () => {
      return (
        <>
            <Nav.Link as={Link} to="/">Landing</Nav.Link>
        </>
      )
    }

    const renderAuthUserNavigation = () => {
        return (
          <>
              <Nav.Link as={Link} to="/home">Home</Nav.Link>
              <Nav.Link as={Link} to="/study">Study</Nav.Link>
          </>
        )
    }

    const renderAuthLinks = () => {
      if(user) {
        return <Button className=".navbar btn" onClick={handleLogout}>Logout</Button>
      }
      else {
        return(
            <Nav.Link as={Link} to="/login">Login</Nav.Link>
        )
      }
    }

    return(
        <Navbar className="custom-navbar" variant="dark" expand="lg">
          <Container>
            <Navbar.Brand as={Link} to="/">LangMaster</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="me-auto">
                { user ? renderAuthUserNavigation() : renderDefaultNavigation() }
              </Nav>
              <Nav>
                { renderAuthLinks() }
              </Nav>
            </Navbar.Collapse>
          </Container>
        </Navbar>
    )
}

export default NavigationBar;