import React from "react";

interface AnswerFeedbackProps {
    isCorrect: boolean;
}

const AnswerFeedback: React.FC<AnswerFeedbackProps> = ({ isCorrect }) => {
    const variant = isCorrect ? 'success' : 'danger';
    const icon = isCorrect ? 'bi-check-circle-fill' : 'bi-x-circle-fill';
    const text = isCorrect ? 'Correct!' : 'Incorrect';

    return(
        <div className={`d-flex align-items-center text-${variant} mt-2 small`}>
            <i className={`me-2 ${icon}`}/>
            <span>{text}</span>
        </div>
    )
}

export default AnswerFeedback;