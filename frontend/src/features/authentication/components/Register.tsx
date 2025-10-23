import React, {useState} from "react";
import styles from "../../../pages/login/loginpage.module.scss";
import {useRegisterMutation} from "../../../shared/api/authApiSlice";
import {CreateUserRequest} from "../../../shared/types/dto";
import {Alert, Button, Form, InputGroup} from "react-bootstrap";
import Blackboard from "../../../shared/components/blackboard/Blackboard";
import Jinny from "../../../shared/components/mascot/common/Jinny";
import { languages, difficulties } from "../../../shared/types/options";
import { mascotGenders } from "../../../shared/types/types";
import { Eye, EyeSlash } from "react-bootstrap-icons";

interface RegisterProps {
    onShowLogin: () => void;
}

const Register: React.FC<RegisterProps> = ({onShowLogin}) => {
    const [username, setUsername] = useState<string>("");
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [language, setLanguage] = useState<string>("");
    const [difficulty, setDifficulty] = useState<string>("");
    const [validated, setValidated] = useState<boolean>(false);
    const [passwordError, setPasswordError] = useState<string>("");
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [ register, {isLoading, error} ] = useRegisterMutation();

    const onRegister = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        setValidated(true);
        setPasswordError("");

        if (password !== confirmPassword) {
            setPasswordError("Passwords do not match.");
            return;
        }

        if(form.checkValidity()) {
            const request: CreateUserRequest = { username, email, password, language, difficulty };
            await register(request).unwrap();
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
                <Blackboard
                    text={isLoading ? "Registering... Please wait. ⏳" : "I'm Jinny! Let's get you signed up for an exciting journey!"}
                    gender={mascotGenders.jinny}
                />
                <Jinny hop={false}/>
            </div>

            { error && (
                <Alert variant="danger">
                    {(error as any)?.data?.message || 'An unexpected error occurred. Please try again.'}
                </Alert>
            )}

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
                <Form.Label htmlFor="register-email">Email</Form.Label>
                <Form.Control
                    required
                    id="register-email"
                    type="email"
                    placeholder="Enter your email here..."
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <Form.Control.Feedback type="invalid">
                    Please provide a valid email.
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-password">Password</Form.Label>
                <InputGroup hasValidation>
                    <Form.Control
                        required
                        id="register-password"
                        pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$"
                        type={isPasswordVisible ? "text" : "password"}
                        placeholder="Enter your password here..."
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <InputGroup.Text onClick={() => setIsPasswordVisible(!isPasswordVisible)} style={{ cursor: 'pointer' }}>
                        {isPasswordVisible ? <EyeSlash size={20}/> : <Eye size={20}/>}
                    </InputGroup.Text>
                    <Form.Control.Feedback type="invalid">
                        Password does not meet the requirements.
                    </Form.Control.Feedback>
                </InputGroup>
                <Form.Text id={styles['passwordHelpBlock']}>
                    Your password must be 8-20 characters long, contain letters and numbers,
                    and must not contain spaces, special characters, or emoji.
                </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="register-confirm-password">Confirm Password</Form.Label>
                <InputGroup hasValidation>
                    <Form.Control
                        required
                        id="register-confirm-password"
                        type={isPasswordVisible ? "text" : "password"}
                        placeholder="Confirm your password..."
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        isInvalid={!!passwordError}
                    />
                    <InputGroup.Text onClick={() => setIsPasswordVisible(!isPasswordVisible)} style={{ cursor: 'pointer' }}>
                        {isPasswordVisible ? <EyeSlash size={20}/> : <Eye size={20}/>}
                    </InputGroup.Text>
                    <Form.Control.Feedback type="invalid">
                        {passwordError || 'Please confirm your password.'}
                    </Form.Control.Feedback>
                </InputGroup>
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
                    { languages.map((lang) => (
                        <option key={lang.value} value={lang.value}>{lang.label}</option>
                    ))}
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
                    { difficulties.map((diff) => (
                        <option key={diff.value} value={diff.value}>{diff.label}</option>
                    ))}
                </Form.Select>
                <Form.Control.Feedback type="invalid">
                    Please select a proficiency level.
                </Form.Control.Feedback>
            </Form.Group>

            <Button type="submit" id={styles['register-button']} className="d-block mx-auto w-100 mt-4" disabled={isLoading}>
                {isLoading ? 'Registering...' : 'Register'}
            </Button>

            <div className="text-center mt-4">
                <Button variant="link" className={`${styles['form-links']}`} onClick={onShowLogin}>Already have an account?</Button>
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

export default Register;