import React, {useEffect, useMemo, useState} from 'react';
import { ReadingComprehensionLessonDTO, QuestionDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';
import {Button, Form} from "react-bootstrap";
import AnswerFeedback from "./extra/AnswerFeedback";
import styles from "./lesson.module.scss";

interface ReadingComprehensionLessonProps {
    lesson: ReadingComprehensionLessonDTO;
    onAllCorrect?: () => void;
}

/**
 * Renders the content for a reading comprehension lesson, displaying the story and questions.
*/
const ReadingComprehensionLesson: React.FC<ReadingComprehensionLessonProps> = ({ lesson, onAllCorrect }) => {
    const {settings} = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const [selectedAnswers, setSelectedAnswers] = useState<{[key: number]: string }>({});
    const [results, setResults] = useState<{[key: number]: boolean } | null>(null);

    useEffect(() => {
        const initialAnswers = lesson.questions.reduce((curr, question) => {
            curr[question.id] = '';
            return curr;
        }, {} as { [key: number]: string});
        setSelectedAnswers(initialAnswers);
        setResults(null);
    }, [lesson]);

    const renderText = (text: string, { as: Component = 'p' as React.ElementType, className = '' } = {}) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    const handleAnswerSelect = (questionId: number, selectedAnswerChoice: string) => {
        setSelectedAnswers(prev => ({...prev, [questionId]: selectedAnswerChoice}));
        setResults(null);
    };

    const handleCheckAnswers = () => {
        const newResults = lesson.questions.reduce((curr, question) => {
            const userAnswer = selectedAnswers[question.id] || '';
            const correctAnswer = question.answer || '';
            curr[question.id] = userAnswer.trim() === correctAnswer.trim();
            return curr;
        }, {} as { [key: number]: boolean });
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
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            {renderText(lesson.story, { className: 'lead' })}
            
            <hr />
            <h6 className="mt-3">Questions:</h6>
            {lesson.questions.map((question: QuestionDTO, index) => {
                const answerChoices = question.answerChoices;

                return(
                    <div key={question.id ?? index} className="mb-4">
                        <p className="mb-2 lead">{index + 1}. {question.questionText}</p>
                        <Form>
                        { answerChoices?.map((choice, choiceIndex) => {
                        return (
                            <Form.Check
                                key={`choice-${question.id}-${choiceIndex}`}
                                inline
                                className={styles.customRadio}
                                label={choice}
                                name={`question-${question.id}`}
                                type={'radio'}
                                id={`choice-${question.id}-${choiceIndex}`}
                                value={choice}
                                checked={selectedAnswers[question.id] === choice}
                                onChange={() => handleAnswerSelect(question.id, choice)}
                            />)
                            })
                        }
                        </Form>
                        {results && <AnswerFeedback isCorrect={results[question.id]}/>}
                    </div>
                )
            })}
            <div className="text-center mt-4">
                <Button onClick={handleCheckAnswers} disabled={!allQuestionsAnswered}>Submit</Button>
            </div>
        </div>
    );
};

export default ReadingComprehensionLesson;