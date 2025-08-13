import React from "react";
import styles from '../styles/homepage.module.css';
import { Container } from "react-bootstrap";

interface LandingPageProps {}

const LandingPage: React.FC<LandingPageProps> = () => {
    return(
        <Container fluid className="py-5">
            <div className={`${styles['glass-container']} mb-4`}>
            <h1 className="text-4xl font-bold mb-4 text-center">Welcome to LangMaster</h1>
            <h5 className="text-lg text-center mb-4">Empower your journey to fluency with immersive tailored lessons and ai-powered language tools.</h5>
            <h3>Ready to Begin?</h3>
            <p>
                Every journey starts with a single step. Here are some tips to make the most of your lessons:
            </p>
            <ul>
                <li>Set a regular study schedule—even 10 minutes a day helps!</li>
                <li>Don’t be afraid to make mistakes. Practice makes progress.</li>
                <li>Try writing your own sentences using new words you learn.</li>
                <li>Speak out loud to improve your pronunciation and confidence.</li>
            </ul>
            <p>
                Let’s make language learning fun and effective together!
            </p>
            </div>
        </Container>
    )
}

export default LandingPage;