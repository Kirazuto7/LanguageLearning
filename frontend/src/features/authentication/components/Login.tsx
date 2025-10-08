import React, { useState } from "react";
import styles from "../../../pages/login/loginpage.module.scss";
import { Form, Button, Alert } from "react-bootstrap";
import Blackboard from "../../../shared/components/blackboard/Blackboard";
import Jinny from "../../../shared/components/mascot/common/Jinny";
import { LoginRequest } from "../../../shared/types/dto";
import { mascotGenders } from "../../../shared/types/types";
import { useLoginMutation } from "../../../shared/api/authApiSlice";

interface LoginProps {
    onShowRegister: () => void;
}

const Login: React.FC<LoginProps> = ({onShowRegister}) => {
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [validated, setValidated] = useState<boolean>(false);
    const [ login, {isLoading, error}] = useLoginMutation();

    const onLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        setValidated(true);

        if (form.checkValidity()) {
            const request: LoginRequest = { username, password };
            await login(request).unwrap();
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
                <Blackboard
                    text={isLoading ? "Logging in...Please wait. ⏳" : "Hi, my name is Jinny! 😄 Would you like to learn a new language with me?"}
                    gender={mascotGenders.jinny}
                />
                <Jinny hop={true} />
            </div>

            { error && (
                <Alert variant="danger">
                    {(error as any)?.data?.message || 'An unexpected error occurred. Please try again.'}
                </Alert>
            )}

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

            <Button type="submit" id={styles['login-button']} className="d-block mx-auto w-100" disabled={isLoading}>
                {isLoading ? 'Logging in...' : 'Login'}
            </Button>

            <div className="text-divider">Or continue with:</div>

            <Button
                variant="light"
                className={`w-100 d-flex align-items-center justify-content-center border ${styles['sso-button']}`}
                href="http://localhost:8080/oauth2/authorization/google"
            >
                <img 
                    src="https://accounts.google.com/favicon.ico" 
                    alt="Google sign-in" 
                    style={{ width: '1.2em', height: '1.2em' }} 
                />
                <span className="ms-2">Sign in with Google</span>
            </Button>

            <div className="text-center mt-4">
                <Button variant="link" className={`${styles['form-links']}`}>Can't log in?</Button>
                <span className={`mx-2 ${styles['form-link-separator']}`}>·</span>
                <Button variant="link" className={`${styles['form-links']}`} onClick={onShowRegister}>Create an account</Button>
            </div>

            <div className={styles['footer-divider']}/>

            <div className={styles['form-footer']}>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles['footer-icon']}>
                    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
                    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
                </svg>
                <div className={styles['footer-brand-container']}>
                    <span className={styles['footer-brand-name']}>Wayword</span>
                    <span className={styles['footer-slogan']}>Craft your language journey.</span>
                </div>
            </div>
        </Form>
    )
}

export default Login;
