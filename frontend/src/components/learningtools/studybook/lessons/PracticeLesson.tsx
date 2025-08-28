import React, {useEffect, useState} from 'react';
import { PracticeLessonDTO, QuestionDTO, PracticeLessonCheckResponse } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';
import {filterInputByLanguage} from "../../../../utils/languageValidation";
import styles from "./lesson.module.scss";
import {useProofread} from "../../../../hooks/useProofread";
import FeedbackDisplay from "./extra/FeedbackDisplay";
import {VolumeUpFill} from "react-bootstrap-icons";
import useTextToSpeech from "../../../../hooks/useTextToSpeech";
import {mascotGenders, MascotName} from "../../../../types/types";

interface PracticeLessonProps {
    lesson: PracticeLessonDTO;
}

/**
 * Renders the content for a practice lesson, displaying open-ended questions for the user.
*/
const PracticeLesson: React.FC<PracticeLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const { speak, cancel } = useTextToSpeech();
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

    const handleSpeak = (text: string) => {
        if (settings?.language) {
            const gender = mascotGenders[settings.mascot as MascotName] || 'female';
            cancel();
            speak(text, settings.language, gender);
        }
    };

    const renderText = (
        text: string,
        { as: Component = 'p' as React.ElementType, className = '' } = {}
    ) => {
        if (isJapanese) {
            // Assuming `text` might contain ruby syntax that needs to be rendered as HTML
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    return (
        <div id={styles.practiceLessonContainer}>
            <h4 className="text-center mb-4">{lesson.title}</h4>

            <h6>{lesson.instructions}</h6>
            {lesson.questions.map((question: QuestionDTO, index) => {
                const answer = answers[question.id] || '';
                const isButtonDisabled = answer.trim() === '';
                const isLoadingProofread = isProofreading && checkingQuestionId === question.id;

                return (
                    <div key={question.id ?? index} className={styles.exampleSentence}>
                        <div className={styles.sentenceNumber}>
                            <div className={styles.circularNumber}>{index + 1}</div>
                        </div>
                        <div className={styles.sentenceContent}>
                            <div className="mb-2">
                                {renderText(question.questionText, { as: 'p', className: styles.nativeSentenceText })}
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
                        <div className={styles.sentenceActions}>
                            <VolumeUpFill className={styles.speakIcon} onClick={() => handleSpeak(question.questionText)}/>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default PracticeLesson;