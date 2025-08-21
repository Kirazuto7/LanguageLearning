import { Link, useNavigate } from 'react-router-dom';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import { Button } from 'react-bootstrap';
import { useLogoutMutation } from '../../features/api/userApiSlice';
import { useSelector } from 'react-redux';
import { RootState } from '../../app/store';
import styles from './navbar.module.scss';

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
              <div className={ `${styles['icon-container']} p-2 bg-primary/10 rounded-lg ${styles['animate-pulse-glow']}`}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                       stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                       className="lucide lucide-book-open w-6 h-6 text-primary">
                      <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                      <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                  </svg>
              </div>
              <Navbar.Text>LangMaster</Navbar.Text>
              <Navbar.Toggle aria-controls="basic-navbar-nav"/>
              <Navbar.Collapse id="basic-navbar-nav">
                  <Nav className="me-auto">
                      {user ? renderAuthUserNavigation() : renderDefaultNavigation()}
                  </Nav>
                  <Nav>
                      {renderAuthLinks()}
                  </Nav>
              </Navbar.Collapse>
          </Container>
        </Navbar>
    )
}

export default NavigationBar;