import React, {useEffect, useState} from 'react';
import { PracticeLessonDTO, QuestionDTO, PracticeLessonCheckResponse } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';
import {filterInputByLanguage} from "../../../../utils/languageValidation";
import styles from "./lesson.module.scss";
import {useProofread} from "../../../../hooks/useProofread";
import FeedbackDisplay from "./extra/FeedbackDisplay";

interface PracticeLessonProps {
    lesson: PracticeLessonDTO;
}

/**
 * Renders the content for a practice lesson, displaying open-ended questions for the user.
*/
const PracticeLesson: React.FC<PracticeLessonProps> = ({ lesson }) => {
    const {settings} = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const [answers, setAnswers] = useState<{[key: number]: string}>({});
    const [feedback, setFeedback] = useState<{[key: number]: PracticeLessonCheckResponse | null}>({});
    const [checkingQuestionId, setCheckingQuestionId] = useState<number | null>(null);

    const { checkSentence, isLoading: isProofreading } = useProofread();

    const placeholderText = `Type your answer in ${settings?.language || 'the target language'}...`;

    useEffect(() => {
        const initialAnswers = lesson.questions.reduce((curr, question) => {
            curr[question.id] = '';
            return curr;
        }, {} as { [key: number]: string});

        const initialFeedback = lesson.questions.reduce((curr, question) => {
            curr[question.id] = null;
            return curr;
        }, {} as { [key: number]: PracticeLessonCheckResponse | null});

        setAnswers(initialAnswers);
        setFeedback(initialFeedback);
    }, [lesson.questions]);

    const handleAnswerChange = (questionId: number, value: string) => {
        const filteredValue = filterInputByLanguage(value, settings?.language);
        setAnswers(prevAnswers => ({...prevAnswers, [questionId]: filteredValue}));
        setFeedback(prev => ({...prev, [questionId]: null})); // Clear feedback when new input is received
    };

    const onSubmit = async (questionId: number) => {
       setCheckingQuestionId(questionId);
       try {
            const result = await checkSentence({questionId, userSentence: answers[questionId]});
            setFeedback(prev => ({ ...prev, [questionId]: result}));
       }
       catch (error) {
            console.error("Proofreading failed:", error);
            setFeedback(prev => ({ ...prev,
                [questionId]: {
                    isCorrect: false,
                    correctedSentence: '',
                    feedback: "An error occurred while checking your answer."
                }
            }));
       }
       finally {
            setCheckingQuestionId(null);
       }
    }

    const renderText = (
        text: string,
        questionNum: number,
        { as: Component = 'p' as React.ElementType, className = '' } = {}
    ) => {
        const questionText = `${questionNum}. ${text}`;
        if (isJapanese) {
            // Assuming `text` might contain ruby syntax that needs to be rendered as HTML
            return <Component className={className} dangerouslySetInnerHTML={{ __html: questionText }} />;
        }
        return <Component className={className}>{questionText}</Component>;
    };

    return (
        <div id={styles.practiceLessonContainer}>
            <h5 className="text-center mb-4">{lesson.title}</h5>

            <p>{lesson.instructions}</p>
            {lesson.questions.map((question: QuestionDTO, index) => {
                const answer = answers[question.id] || '';
                const isButtonDisabled = answer.trim() === '';
                const isLoadingProofread = isProofreading && checkingQuestionId === question.id;

                return (
                    <div key={question.id ?? index} className="mb-4">
                        <div className="mb-2">
                            {renderText(question.questionText, index + 1, { className: 'lead' })}
                        </div>
                        <div className="d-flex flex-row align-items-center">
                            <input className={styles.practiceInput}
                                   type="text"
                                   placeholder={placeholderText}
                                   value={answer}
                                   onChange={(e) => handleAnswerChange(question.id, e.target.value)}
                                   />
                            <button
                                className={`${styles.checkButton} ms-2`}
                                onClick={() => onSubmit(question.id)}
                                disabled={isButtonDisabled || isLoadingProofread}
                                >
                                {isLoadingProofread ?
                                    (
                                        <div className="spinner-border spinner-border-sm" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                    ) :
                                    <i className="bi-send-check-fill"/>
                                }
                            </button>
                        </div>
                        <FeedbackDisplay feedback={feedback[question.id]} isLoading={isLoadingProofread}/>
                    </div>
                );
            })}
        </div>
    );
};

export default PracticeLesson;