import React, {useEffect, useMemo, useState} from 'react';
import { ReadingComprehensionLessonDTO, QuestionDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';
import {Button, Form, Card} from "react-bootstrap";
import AnswerFeedback from "./extra/AnswerFeedback";
import styles from "./lesson.module.scss";
import useTextToSpeech from "../../../../hooks/useTextToSpeech";
import { mascotGenders, MascotName } from "../../../../types/types";
import { VolumeUpFill, Book } from "react-bootstrap-icons";

interface ReadingComprehensionLessonProps {
    lesson: ReadingComprehensionLessonDTO;
    onAllCorrect?: () => void;
}

/**
 * Renders the content for a reading comprehension lesson, displaying the story and questions.
*/
const ReadingComprehensionLesson: React.FC<ReadingComprehensionLessonProps> = ({ lesson, onAllCorrect }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const [selectedAnswers, setSelectedAnswers] = useState<{[key: string]: string }>({});
    const [results, setResults] = useState<{[key: string]: boolean } | null>(null);

    useEffect(() => {
        const initialAnswers = lesson.questions.reduce((curr, question) => {
            curr[question.id] = '';
            return curr;
        }, {} as { [key: string]: string});
        setSelectedAnswers(initialAnswers);
        setResults(null);
    }, [lesson]);

    const handleSpeak = (text: string) => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            cancel();
            speak(text, settings.language, gender);
        }
    };

    const renderText = (text: string, { as: Component = 'p' as React.ElementType, className = '' } = {}) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    const handleAnswerSelect = (questionId: string, selectedAnswerChoice: string) => {
        setSelectedAnswers(prev => ({...prev, [questionId]: selectedAnswerChoice}));
        setResults(null);
    };

    const handleCheckAnswers = () => {
        const newResults = lesson.questions.reduce((curr, question) => {
            const userAnswer = selectedAnswers[question.id] || '';
            const correctAnswer = question.answer || '';
            curr[question.id] = userAnswer.trim() === correctAnswer.trim();
            return curr;
        }, {} as { [key: string]: boolean });
        setResults(newResults);

        const allCorrect = Object.values(newResults).every(result => result === true);

        if (allCorrect && lesson.questions.length > 0 && onAllCorrect) {
            console.log("TEST!");
            onAllCorrect();
        }
    };

    const allQuestionsAnswered = useMemo(() => {
        if (lesson.questions.length === 0) {
            return false;
        }
        // Checks if all questions have been answered before enabling the submit button
        return Object.values(selectedAnswers).every(answer => answer !== '');
    }, [selectedAnswers, lesson.questions])

    return (
        <div>
            <h2 className="text-center mb-4">{lesson.title}</h2>
            <div className={styles.storyContainer}>
                {renderText(lesson.story, { as: 'p', className: styles.storyText })}
                <div className="text-end">
                    <VolumeUpFill className={styles.speakIcon} onClick={() => handleSpeak(lesson.story)}/>
                </div>
            </div>

            <div className={styles.fancyDivider}>
                <Book size={24}/>
            </div>

            <h6>Questions:</h6>
            {lesson.questions.map((question: QuestionDTO, index) => {
                const answerChoices = question.answerChoices;

                return(
                    <Card key={question.id ?? index} className={styles.questionCard}>
                        <Card.Header className={styles.questionCardHeader}>
                            <span>Question {index + 1}</span>
                        </Card.Header>
                        <Card.Body>
                            <p className={styles.questionText}>{question.questionText}</p>
                            <Form>
                                { answerChoices?.map((choice, choiceIndex) => (
                                    <Form.Check
                                        key={`choice-${question.id}-${choiceIndex}`}
                                        className={styles.customRadio}
                                        label={choice}
                                        name={`question-${question.id}`}
                                        type={'radio'}
                                        id={`choice-${question.id}-${choiceIndex}`}
                                        value={choice}
                                        checked={selectedAnswers[question.id] === choice}
                                        onChange={() => handleAnswerSelect(question.id, choice)}
                                    />
                                ))}
                            </Form>
                        </Card.Body>
                        {results && <Card.Footer><AnswerFeedback isCorrect={results[question.id]}/></Card.Footer>}
                    </Card>
                )
            })}
            <div className="text-center mt-4">
                <Button onClick={handleCheckAnswers} disabled={!allQuestionsAnswered}>Submit</Button>
            </div>
        </div>
    );
};

export default ReadingComprehensionLesson;