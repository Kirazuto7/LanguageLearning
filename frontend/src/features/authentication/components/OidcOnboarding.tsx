import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useCompleteOidcRegistrationMutation } from "../../../shared/api/authApiSlice";
import { CompleteOidcRegistrationRequest } from "../../../shared/types/dto";
import { Alert, Button, Container, Form, Spinner } from "react-bootstrap";
import { languages, difficulties } from "../../../shared/types/options";
import styles from "../../../pages/login/loginpage.module.scss";
import {logToServer} from "../../../shared/utils/loggingService";
import Blackboard from "../../../shared/components/blackboard/Blackboard";
import {mascotGenders} from "../../../shared/types/types";
import Jinny from "../../../shared/components/mascot/common/Jinny";

interface OnboardingTokenPayload {
    name: string;
    email: string;
}

const OidcOnboarding: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const [username, setUsername] = useState<string>("");
    const [language, setLanguage] = useState<string>("");
    const [difficulty, setDifficulty] = useState<string>("");
    const [onboardingToken, setOnboardingToken] = useState<string | null>(null);
    const [userInfo, setUserInfo] = useState<OnboardingTokenPayload | null>(null);
    const [validated, setValidated] = useState<boolean>(false);
    const [completeRegistration, { isLoading, error }] = useCompleteOidcRegistrationMutation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const token = params.get("token");

        if (token) {
            try {
                const decoded: OnboardingTokenPayload = jwtDecode(token);
                setUserInfo(decoded);
                setOnboardingToken(token);
                setUsername(decoded.email.split('@')[0] || "");
            }
            catch (e) {
                logToServer('error', "Invalid onboarding token.", e);
                navigate("/login");
            }
        }
        else {
            logToServer('warn', "No onboarding token found.");
            navigate("/login");
        }
    }, [location, navigate]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        setValidated(true);

        if (form.checkValidity() && onboardingToken) {
            const request: CompleteOidcRegistrationRequest = {
                onboardingToken,
                username,
                language,
                difficulty
            };
            try  {
                await completeRegistration(request).unwrap();
                navigate("/home");
            }
            catch (err) {}
        }
    };

    if (!userInfo) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    return(
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 72px)', padding: '2rem 1rem' }}>
            <Form
                id={styles['register-form']}
                className="glass-container"
                onSubmit={handleSubmit}
                validated={validated}
                noValidate
            >
                <div className="d-flex justify-content-center align-items-center mb-5">
                    <Blackboard
                        text={isLoading ? "Saving your preferences... ⏳" : `Welcome, ${userInfo.name}! Just a few more details to get you started.`}
                        gender={mascotGenders.jinny}
                    />
                    <Jinny hop={false}/>
                </div>

                {error && (
                    <Alert variant="danger">
                        {(error as any)?.data?.message || 'An unexpected error occurred. Please try again.'}
                    </Alert>
                )}

                <Form.Group className="mb-3">
                    <Form.Label htmlFor="oidc-username">Choose a Username</Form.Label>
                    <Form.Control
                        required
                        id="oidc-username"
                        type="text"
                        placeholder="Enter your username..."
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <Form.Control.Feedback type="invalid">
                        Please choose a username.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label htmlFor="oidc-language">Language</Form.Label>
                    <Form.Select
                        required
                        id="oidc-language"
                        value={language}
                        onChange={(e) => setLanguage(e.target.value)}
                    >
                        <option value="" disabled>Which language do you want to learn?</option>
                        {languages.map((lang) => (
                            <option key={lang.value} value={lang.value}>{lang.label}</option>
                        ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                        Please select a language.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label htmlFor="oidc-difficulty">Level</Form.Label>
                    <Form.Select
                        required
                        id="oidc-difficulty"
                        value={difficulty}
                        onChange={(e) => setDifficulty(e.target.value)}
                    >
                        <option value="" disabled>What is your proficiency?</option>
                        {difficulties.map((diff) => (
                            <option key={diff.value} value={diff.value}>{diff.label}</option>
                        ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                        Please select a proficiency level.
                    </Form.Control.Feedback>
                </Form.Group>

                <Button type="submit" className="w-100 mt-4 justify-content-center" disabled={isLoading}>
                    {isLoading ? "Completing Registration..." : "Start Learning"}
                </Button>

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
        </Container>
    );
}

export default OidcOnboarding;