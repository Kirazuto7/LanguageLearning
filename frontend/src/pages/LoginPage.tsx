import React, { useState, useEffect } from "react";
import styles from "../styles/loginpage.module.css";
import { Container, Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MascotCharacter, Blackboard }  from "../components/Mascot";
import { useUser } from "../contexts/UserContext";
import { LoginRequest, CreateUserRequest } from "../types/dto";

interface LoginProps {
    onShowRegister: () => void;
}

const Login: React.FC<LoginProps> = ({onShowRegister}) => {
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [validated, setValidated] = useState<boolean>(false);
    const { login, isLoading, error } = useUser();

    const onLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        setValidated(true);
        if(form.checkValidity() === true) {
            const request: LoginRequest = { username, password };
            await login(request);
        } 
    }

    return(
        <Form 
            id={styles['login-form']} 
            className="glass-container" 
            onSubmit={onLogin}
            validated={validated}
            noValidate
        >
            <div className="d-flex justify-content-center align-items-center mb-5">
                <Blackboard text={isLoading ? "Logging in...Please wait. ⏳" : `Hi my name is Aya 😄. Would you like to learn a new language with me?`}/>
                <MascotCharacter hop={true} />
            </div>

            { error && <Alert variant="danger">{error}</Alert> }

            <Form.Group className="mb-3">
                <Form.Label htmlFor="login-username">Username</Form.Label>
                <Form.Control
                    required
                    id="login-username"
                    type="text"
                    placeholder="Enter your username here..."
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <Form.Control.Feedback type="invalid">
                    Please enter your username.
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="login-password">Password</Form.Label>
                <Form.Control
                    required
                    id="login-password"
                    type="password" 
                    placeholder="Enter your password here..."
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <Form.Control.Feedback type="invalid">
                    Please enter your password.
                </Form.Control.Feedback>
            </Form.Group>

            <Button type="submit" id={styles['login-button']} className="d-block mx-auto mb-5" disabled={isLoading}>
                {isLoading ? 'Logging in...' : 'Login'}
            </Button>
            <div className="divider"/>
            <div className="d-flex flex-column align-items-center">
                <Button variant="link" className={`${styles['form-links']}`} onClick={onShowRegister}>Sign up</Button>
                <Button variant="link" className={`${styles['form-links']}`}>Forgot password</Button>
            </div>
        </Form>
    )
}

interface RegisterProps {
    onShowLogin: () => void;
}

const Register: React.FC<RegisterProps> = ({onShowLogin}) => {
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [language, setLanguage] = useState<string>("");
    const [difficulty, setDifficulty] = useState<string>("");
    const [validated, setValidated] = useState<boolean>(false);
    const { register, isLoading, error } = useUser();

    const onRegister = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        setValidated(true);
        if(form.checkValidity() === true) {
            const request: CreateUserRequest = { username, password, language, difficulty };
            await register(request);
        }
    }

    return(
        <Form 
            id={styles['register-form']} 
            className="glass-container" 
            onSubmit={onRegister}
            validated={validated}
            noValidate
        >
            <div className="d-flex justify-content-center align-items-center mb-5">
                <Blackboard text={isLoading ? "Registering...Please wait. ⏳" : `Ready to sign up? This is going to be an exciting journey!`}/>
                <MascotCharacter hop={false} />
            </div>

            { error && <Alert variant="danger">{error}</Alert> }

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-username">Username</Form.Label>
                <Form.Control
                    required
                    id="register-username"
                    type="text"
                    placeholder="Enter your username here..."
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <Form.Control.Feedback type="invalid">
                    Please enter a username.
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-password">Password</Form.Label>
                <Form.Control
                    required
                    id="register-password"
                    pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$"
                    type="password"
                    placeholder="Enter your password here..."
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <Form.Control.Feedback type="invalid">
                    Password does not meet the requirements.
                </Form.Control.Feedback>
                <Form.Text id={styles['passwordHelpBlock']}>
                    Your password must be 8-20 characters long, contain letters and numbers,
                    and must not contain spaces, special characters, or emoji.
                </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-language">Language</Form.Label>
                <Form.Select
                    required
                    id="register-language"
                    aria-label="Language Form Select"
                    value={language}
                    onChange={(e) => setLanguage(e.target.value)}
                >
                    <option value="" disabled>Which language are you most interested in?</option>
                    <option value="Korean">Korean</option>
                    <option value="Japanese">Japanese</option>
                </Form.Select>
                <Form.Control.Feedback type="invalid">
                    Please select a language.
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-difficulty">Level</Form.Label>
                <Form.Select
                    required
                    id="register-difficulty"
                    aria-label="Language Level Form Select"
                    value={difficulty}
                    onChange={(e) => setDifficulty(e.target.value)}
                >
                    <option value="" disabled>What is your proficiency?</option>
                    <option value="Beginner">Beginner</option>
                    <option value="Pre-Intermediate">Pre-Intermediate</option>
                    <option value="Intermediate">Intermediate</option>
                    <option value="Advanced">Advanced</option>
                </Form.Select>
                <Form.Control.Feedback type="invalid">
                    Please select a proficiency level.
                </Form.Control.Feedback>
            </Form.Group>

            <Button type="submit" id={styles['register-button']} className="d-block mx-auto mb-5" disabled={isLoading}>
                {isLoading ? 'Registering...' : 'Register'}
            </Button>
            <div className="divider"/>
            <div className="d-flex flex-column align-items-center">
                <Button variant="link" className={`${styles['form-links']}`} onClick={onShowLogin}>Login</Button>
            </div>
        </Form>
    )
}

const LoginPage = () => {
    const { user } = useUser();
    const navigate = useNavigate();
    const [showRegister, setShowRegister] = useState<boolean>(false);

    // After a successful login/registration, the user object in the context will be populated.
    // This effect will then trigger, redirecting the user to the home page.
    useEffect(() => {
        if (user) navigate('/home');
    }, [user, navigate]);

    const onShowRegister = () => {
        setShowRegister(true);
    }

    const onShowLogin = () => {
        setShowRegister(false);
    }

    return(
        <Container fluid id={styles['login-page-container']}>
            {showRegister ?  <Register onShowLogin={onShowLogin}/>: <Login onShowRegister={onShowRegister}/>}
        </Container>
    )
}

export default LoginPage;
