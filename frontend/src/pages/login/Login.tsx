import React, { useState } from "react";
import styles from "./loginpage.module.scss";
import { Form, Button, Alert } from "react-bootstrap";
import Blackboard from "../../components/ai/Blackboard";
import Jinny from "../../components/ai/mascots/Jinny";
import { LoginRequest } from "../../types/dto";
import { useLoginMutation } from "../../features/api/userApiSlice";

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
                    text={isLoading ? "Logging in...Please wait. ⏳" : `Hi my name is Jinny 😄. Would you like to learn a new language with me?`}
                    gender={'female'}
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

            <Button type="submit" id={styles['login-button']} className="d-block mx-auto" disabled={isLoading}>
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

export default Login;
