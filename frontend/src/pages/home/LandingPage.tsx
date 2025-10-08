import React from "react";
import { Card, Container } from "react-bootstrap";

interface LandingPageProps {}

const LandingPage: React.FC<LandingPageProps> = () => {
    return(
        <Container fluid className="d-flex flex-column justify-content-center align-items-center flex-grow-1 py-5">
            <div className={"glass-container mb-4"} style={{ maxWidth: '800px'}}>
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

            <div className="glass-container mb-4">
                <h2 className="text-center">Language Tools Collection</h2>
                <div className="divider mb-4"/>
                <Card>
                    <Card.Header>
                        <Card.Title className="text-center fw-bold">Your Personal AI-Powered Textbook</Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <p>Experience a classic, structured learning path with a modern twist. Our AI generates comprehensive lesson books on any topic you choose, complete with vocabulary, grammar explanations, and practice exercises—all tailored to your level.</p>
                        <Card.Subtitle>Features:</Card.Subtitle>
                        <ul>
                            <li><b>Personalized Topics:</b> From "Korean Street Food" to "Italian Renaissance Art," learn what you're passionate about.</li>
                            <li><b>Structured Chapters:</b> Each book is organized into logical chapters, covering vocabulary, grammar, and practical exercises.</li>
                            <li><b>Interactive Practice:</b> Test your knowledge with AI-powered proofreading exercises that provide instant corrections and feedback.</li>
                            <li><b>All Levels Welcome:</b> Whether you're a beginner or advanced, the content is tailored to your proficiency.</li>
                        </ul>
                    </Card.Body>
                </Card>

                <Card className={"mt-4"}>
                    <Card.Header>
                        <Card.Title className="text-center fw-bold">Live the Language with Illustrated Storybooks</Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <p>Dive into captivating, AI-generated short stories that bring the language to life. Each story is created just for you, complete with beautiful illustrations and interactive vocabulary support to make learning feel less like studying and more like an adventure.</p>
                        <Card.Subtitle>Features:</Card.Subtitle>
                        <ul>
                            <li><b>Visually Stunning Stories:</b> AI-generated images illustrate each page, creating an immersive reading experience.</li>
                            <li><b>Contextual Vocabulary List:</b> Key vocabulary from the story is collected at the bottom of each page, complete with translations, allowing you to review new words without losing your place.</li>
                            <li><b>Linguistically Smart Highlighting:</b> Our backend uses advanced linguistic analysis to accurately identify and highlight conjugated word forms, ensuring you learn vocabulary in its natural context.</li>
                        </ul>
                    </Card.Body>
                </Card>

                <Card className={"mt-4"}>
                    <Card.Header>
                        <Card.Title className="text-center fw-bold">Quick-Check with the Translation Tool</Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <p>Have a word or phrase you need to understand quickly? Our simple and effective translation tool provides instant translations to and from your target language, acting as a perfect companion for your learning journey.</p>
                        <Card.Subtitle>Features:</Card.Subtitle>
                        <ul>
                            <li><b>Fast & Accurate:</b> Get quick translations for words and sentences when you need them most.</li>
                            <li><b>Convenient Companion:</b> A great utility for moments when you need a quick lookup without breaking your study flow.</li>
                        </ul>
                    </Card.Body>
                </Card>
            </div>
        </Container>
    )
}

export default LandingPage;