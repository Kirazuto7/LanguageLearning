import {useSelector} from "react-redux";
import {RootState} from "../../app/store";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import Container from "react-bootstrap/Container";
import Login from "./Login";
import Register from "./Register";
import styles from './loginpage.module.scss';

const LoginPage = () => {
    const { user } = useSelector((state: RootState) => state.auth);
    const navigate = useNavigate();
    const [showRegister, setShowRegister] = useState<boolean>(false);

    // After a successful login/registration, the user object in the context will be populated.
    // This effect will then trigger, redirecting the user to the home page.
    useEffect(() => {
        if (user) navigate('/home');
    }, [user, navigate]);

    const showRegisterForm = () => setShowRegister(true);
    const showLoginForm = () => setShowRegister(false);

    return(
        <Container fluid id={styles['login-page-container']}>
            {showRegister ? <Register onShowLogin={showLoginForm} /> : <Login onShowRegister={showRegisterForm} />}
        </Container>
    )
}

export default LoginPage;