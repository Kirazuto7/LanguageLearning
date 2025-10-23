import { Link, useNavigate } from 'react-router-dom';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import { Button } from 'react-bootstrap';
import { useLogoutMutation } from '../../shared/api/authApiSlice';
import { useDispatch, useSelector } from 'react-redux';
import {AppDispatch, RootState} from '../../app/store';
import styles from './navbar.module.scss';
import LearningToolsNavigation from "../toolsNavigationBar/LearningToolsNavigation";
import Settings from "../../features/userSettings/components/Settings";
import {useState} from "react";
import {logOut} from "../../features/authentication/authSlice";
import {logToServer} from "../../shared/utils/loggingService";

const NavigationBar = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [openSettings, setOpenSettings] = useState(false);
  const [logout] = useLogoutMutation();
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

    const handleLogout = async() => {
      try {
        await logout().unwrap();
      }
      catch(err) {
        logToServer('error', "Failed to logout.", err);

      }
      finally {
          dispatch(logOut());
          navigate('/login');
      }
    }

    return(
        <Navbar className={styles.customNavbar} expand="lg">
          <Container fluid>
              <Navbar.Brand as={Link} to={user ? "/home" : "/"} className="d-flex align-items-center ms-3">
                  <div className={`${styles['icon-container']} ${styles['animate-pulse-glow']}`}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                       stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
                       className={`lucide lucide-book-open ${styles.icon}`}>
                      <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                      <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                  </svg>
                  </div>
                  <span className={`fw-bold ms-2 ${styles['brand-name']}`}>Wayword</span>
              </Navbar.Brand>
              <Navbar.Toggle aria-controls="basic-navbar-nav"/>
              <Navbar.Collapse id="basic-navbar-nav">
                  {user && (
                      <Nav className="mx-auto">
                          <LearningToolsNavigation />
                      </Nav>
                  )}
                  <Nav className="ms-auto align-items-center gap-4">
                      {user ? (
                         <>
                            <Settings openSettings={openSettings} setOpenSettings={setOpenSettings} />
                            <Button size="sm" className="btn-outline" onClick={handleLogout}>Logout</Button>
                         </>
                      ) :
                      (
                         <Nav.Link as={Link} to="/login">Login</Nav.Link>
                      )}
                  </Nav>
              </Navbar.Collapse>
          </Container>
        </Navbar>
    )
}

export default NavigationBar;