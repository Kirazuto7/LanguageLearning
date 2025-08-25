import React, {useEffect, useState} from 'react';
import { PracticeLessonDTO, QuestionDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';
import {filterInputByLanguage} from "../../../../utils/languageValidation";
import styles from "./lesson.module.scss";

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
    const placeholderText = `Type your answer in ${settings?.language || 'the target language'}...`;

    useEffect(() => {
        const initialAnswers = lesson.questions.reduce((curr, question) => {
            curr[question.id] = '';
            return curr;
        }, {} as { [key: number]: string});
        setAnswers(initialAnswers);
    }, [lesson.questions]);

    const handleAnswerChange = (questionId: number, value: string) => {
        const filteredValue = filterInputByLanguage(value, settings?.language);
        setAnswers(prevAnswers => ({...prevAnswers, [questionId]: filteredValue}));
    };

    const onSubmit = (questionId: number) => {
        console.log(answers[questionId]);
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
                                disabled={isButtonDisabled}
                                >
                                <i className="bi-send-check-fill"/>
                            </button>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default PracticeLesson;